package net.mcreator.concoction.block.entity;

import com.google.gson.Gson;
import net.mcreator.concoction.block.OvenBlock;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipeInput;
import net.mcreator.concoction.world.inventory.OvenGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import org.jetbrains.annotations.Nullable;
import io.netty.buffer.Unpooled;

import java.util.*;

public class OvenBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    // Слоты: 0 бутылочка, 1-6 крафт, 7 миска, 8 результат
    private final int ContainerSize = 9;
    private boolean isCooking = false;
    private RecipeHolder<OvenRecipe> recipe = null;
    private Map<String, String> craftResult = Map.ofEntries(
            Map.entry("id",""),
            Map.entry("count",""),
            Map.entry("interactionType","")
    );

    private int progress = 0;
    private int maxProgress = 200;
    private final int DEFAULT_MAX_PROGRESS = 200;

    private NonNullList<ItemStack> items = NonNullList.withSize(
            this.ContainerSize,
            ItemStack.EMPTY
    );

    // --- Константы слотов для удобства
    private static final int SLOT_BOTTLE = 0;
    private static final int SLOT_FIRST_CRAFT = 1; // 1..6 — слоты ингредиентов
    private static final int SLOT_LAST_CRAFT  = 6;
    private static final int SLOT_BOWL = 7;
    private static final int SLOT_OUTPUT = 8;

    // *** массив крафтовых слотов для удобства (используется для воронок сверху)
    private static final int[] SLOTS_INGREDIENTS = new int[]{
            SLOT_FIRST_CRAFT, SLOT_FIRST_CRAFT + 1, SLOT_FIRST_CRAFT + 2,
            SLOT_FIRST_CRAFT + 3, SLOT_FIRST_CRAFT + 4, SLOT_LAST_CRAFT
    };

    public OvenBlockEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.OVEN_BLOCK.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("cooking.progress");
        this.maxProgress = tag.getInt("cooking.max_progress");
        this.isCooking = tag.getBoolean("cooking.is_cooking");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.craftResult = (new Gson()).fromJson(tag.getString("cooking.craft_result"), HashMap.class);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cooking.progress", this.progress);
        tag.putInt("cooking.max_progress", this.maxProgress);
        tag.putBoolean("cooking.is_cooking", this.isCooking);
        tag.putString("cooking.craft_result", (new Gson()).toJson(this.craftResult));

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    /**
     * Тик духовки:
     *  - БОЛЬШЕ НЕ ЗАВИСИТ ОТ ТЕПЛА СНИЗУ
     *  - если есть валидный рецепт и есть место под результат → готовим и загораемся
     *  - иначе прогресс сбрасывается и духовка не горит
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // ищем подходящий рецепт под текущие ингредиенты
        Optional<RecipeHolder<OvenRecipe>> currentRecipe = getCurrentRecipe();

        if (currentRecipe.isEmpty()) {
            // рецепта нет — стоп и гасим
            if (this.isCooking) {
                resetProgress();
            }
            updateLit(level, pos, state, false);
            return;
        }

        RecipeHolder<OvenRecipe> rh = currentRecipe.get();

        // если рецепт сменился — сбрасываем прогресс и подхватываем новый
        if (this.recipe == null || !isSameRecipe(rh, this.recipe)) {
            this.recipe = rh;
            this.progress = 0;
            this.maxProgress = rh.value().getCookingTime();
            this.isCooking = false;
            setChanged();
        }

        // если некуда класть результат — не готовим и не горим
        if (!canAddResult()) {
            this.isCooking = false;
            updateLit(level, pos, state, false);
            return;
        }

        // готовим
        this.isCooking = true;
        updateLit(level, pos, state, true);

        increaseCraftingProgress();

        if (hasCraftingFinished()) {
            craftItem();
            resetProgress();
        }
    }

    /**
     * Обновляет флаг LIT у блока в мире.
     */
    private void updateLit(Level level, BlockPos pos, BlockState state, boolean shouldBeLit) {
        if (!state.hasProperty(OvenBlock.LIT)) return;
        boolean wasLit = state.getValue(OvenBlock.LIT);
        if (wasLit != shouldBeLit) {
            level.setBlock(pos, state.setValue(OvenBlock.LIT, shouldBeLit), 3);
        }
    }

    private boolean isSameRecipe(RecipeHolder<OvenRecipe> recipe1, RecipeHolder<OvenRecipe> recipe2) {
        if (recipe1 == null || recipe2 == null) return false;

        // Сравниваем по ID рецептов (основной способ)
        if (recipe1.id().equals(recipe2.id())) return true;

        // Если ID разные, сравниваем по ингредиентам
        List<Ingredient> ingredients1 = recipe1.value().getIngredients();
        List<Ingredient> ingredients2 = recipe2.value().getIngredients();

        if (ingredients1.size() != ingredients2.size()) return false;

        // Создаем копии списков для сравнения
        List<Ingredient> sorted1 = new ArrayList<>(ingredients1);
        List<Ingredient> sorted2 = new ArrayList<>(ingredients2);

        // Сортируем по строковому представлению для сравнения
        sorted1.sort((a, b) -> a.toString().compareTo(b.toString()));
        sorted2.sort((a, b) -> a.toString().compareTo(b.toString()));

        for (int i = 0; i < sorted1.size(); i++) {
            if (!sorted1.get(i).toString().equals(sorted2.get(i).toString())) {
                return false;
            }
        }

        return true;
    }

    private boolean canAddResult() {
        if (recipe == null) return false;

        ItemStack resultSlot = items.get(SLOT_OUTPUT);
        Map<String, String> recipeResult = recipe.value().getResult();

        if (resultSlot.isEmpty()) {
            return true;
        }

        // Проверяем, совпадает ли предмет в слоте результата с результатом рецепта
        ResourceLocation resultId = ResourceLocation.parse(recipeResult.get("id"));
        if (!BuiltInRegistries.ITEM.get(resultId).equals(resultSlot.getItem())) {
            return false;
        }

        // Проверяем, поместится ли результат
        int resultCount = parseResultCount(recipeResult);
        return resultSlot.getCount() + resultCount <= resultSlot.getMaxStackSize();
    }

    private int parseResultCount(Map<String, String> recipeResult) {
        try {
            return Math.max(1, Integer.parseInt(recipeResult.getOrDefault("count","1").trim()));
        } catch (Exception e) {
            return 1;
        }
    }

    /** Сколько мисок нужно списывать за одну готовку (если миска требуется рецептом). */
    private int bowlsToConsumePerCraft(RecipeHolder<OvenRecipe> rh) {
        if (rh == null) return 0;
        if (rh.value().getBowlIngredient() == null || rh.value().getBowlIngredient().isEmpty()) return 0;
        return parseResultCount(rh.value().getResult());
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = recipe == null ? DEFAULT_MAX_PROGRESS : recipe.value().getCookingTime();
        this.isCooking = false;
        this.recipe = null;
        this.craftResult = Map.of("id", "", "count", "", "interactionType", "");
        setChanged();
    }

    private void resetProgressOnly() {
        this.progress = 0;
        this.isCooking = false;
        setChanged();
    }

    private void craftItem() {
        if (recipe == null) return;

        Map<String, String> recipeResult = recipe.value().getResult();
        ResourceLocation resultId = ResourceLocation.parse(recipeResult.get("id"));
        int resultCount = parseResultCount(recipeResult);

        // Создаем результат
        ItemStack result = new ItemStack(BuiltInRegistries.ITEM.get(resultId), resultCount);

        // Добавляем в слот результата
        ItemStack resultSlot = items.get(SLOT_OUTPUT);
        if (resultSlot.isEmpty()) {
            items.set(SLOT_OUTPUT, result);
        } else {
            resultSlot.grow(resultCount);
        }

        // Тратим ингредиенты (с учётом количества мисок = числу результата)
        consumeIngredients(recipe);

        setChanged();
    }

    /**
     * Списывает входные ингредиенты.
     *  - слот миски (7) тратит миски/ведра/посуду без возврата контейнера;
     *  - в остальных слотах контейнеры возвращаются.
     */
    private void consumeIngredients(RecipeHolder<OvenRecipe> rh) {
        if (rh == null || level == null) return;

        int bowlsPerCraft = bowlsToConsumePerCraft(rh);

        List<ItemStack> containers = new ArrayList<>();

        // Списываем слоты 0..7 (включая миску), но контейнеры возвращаем не из миски
        for (int i = 0; i <= SLOT_BOWL; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            int shrinkAmount = 1;
            if (i == SLOT_BOWL && bowlsPerCraft > 0) {
                shrinkAmount = bowlsPerCraft;
            }

            int toRemove = Math.min(stack.getCount(), shrinkAmount);
            if (toRemove <= 0) continue;

            if (i != SLOT_BOWL) {
                if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "bottles")))) {
                    ItemStack drop = new ItemStack(Items.GLASS_BOTTLE, toRemove);
                    containers.add(drop);
                } else if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "buckets")))) {
                    ItemStack drop = new ItemStack(Items.BUCKET, toRemove);
                    containers.add(drop);
                } else if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "bowls")))) {
                    ItemStack drop = new ItemStack(Items.BOWL, toRemove);
                    containers.add(drop);
                }
            }

            stack.shrink(toRemove);
            if (stack.isEmpty()) {
                items.set(i, ItemStack.EMPTY);
            }
        }

        // выбрасываем пустые контейнеры в мир
        for (ItemStack container : containers) {
            if (level != null && !level.isClientSide && !container.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 1.0,
                        worldPosition.getZ() + 0.5,
                        container);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
        setChanged();
    }

    private Optional<RecipeHolder<OvenRecipe>> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        return this.level.getRecipeManager()
                .getRecipeFor(ConcoctionModRecipes.OVEN_RECIPE_TYPE.get(),
                        new OvenRecipeInput(this.getItems()), level);
    }

    @Override
    public int getContainerSize() {
        return this.ContainerSize;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previousStack = this.items.get(slot);
        boolean isSlotEmpty = previousStack.isEmpty();
        boolean isStackEmpty = stack.isEmpty();
        boolean isDifferentItem = !isSlotEmpty && !isStackEmpty &&
                (!ItemStack.matches(previousStack, stack));

        boolean isIngredientSlot = slot >= 0 && slot < 8;

        if (isIngredientSlot && (isDifferentItem || isSlotEmpty != isStackEmpty)) {

            ItemStack oldStack = this.items.get(slot);

            ItemStack testStack = oldStack.copy();
            if (!testStack.isEmpty()) {
                testStack.setCount(1);
            }
            this.items.set(slot, testStack);

            Optional<RecipeHolder<OvenRecipe>> originalRecipe = getCurrentRecipe();

            this.items.set(slot, stack);
            Optional<RecipeHolder<OvenRecipe>> newRecipe = getCurrentRecipe();

            boolean shouldReset = false;

            if (this.isCooking && this.recipe != null) {
                if (originalRecipe.isPresent() && isSameRecipe(originalRecipe.get(), this.recipe)) {
                    shouldReset = false;
                } else if (newRecipe.isPresent()) {
                    if (!isSameRecipe(newRecipe.get(), this.recipe)) {
                        shouldReset = true;
                    }
                } else {
                    shouldReset = true;
                }
            }

            this.items.set(slot, oldStack);

            if (shouldReset) {
                resetProgressOnly();
            }
        }

        stack.limitSize(this.getMaxStackSize(stack));
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> Items) {
        this.items = Items;
        this.setChanged();
    }

    // === WorldlyContainer логика ===

    @Override
    public int[] getSlotsForFace(Direction side) {
        BlockState state = this.getBlockState();
        Direction facing = Direction.NORTH;
        if (state.hasProperty(OvenBlock.FACING)) {
            facing = state.getValue(OvenBlock.FACING);
        }

        if (side == Direction.UP) {
            return SLOTS_INGREDIENTS;
        }

        if (side == Direction.DOWN) {
            return new int[]{SLOT_OUTPUT};
        }

        if (side == facing.getOpposite()) {
            return new int[]{SLOT_BOWL};
        }

        if (side == facing.getClockWise() || side == facing.getCounterClockWise()) {
            return new int[]{SLOT_BOTTLE};
        }

        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction side) {
        if (side == null) return false;

        BlockState state = this.getBlockState();
        Direction facing = Direction.NORTH;
        if (state.hasProperty(OvenBlock.FACING)) {
            facing = state.getValue(OvenBlock.FACING);
        }

        if (side == Direction.UP) {
            return index >= SLOT_FIRST_CRAFT && index <= SLOT_LAST_CRAFT;
        }

        if (side == facing.getOpposite()) {
            if (index != SLOT_BOWL) return false;
            return stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tableware")));
        }

        if (side == facing.getClockWise() || side == facing.getCounterClockWise()) {
            return index == SLOT_BOTTLE;
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side) {
        if (side == Direction.DOWN) {
            return index == SLOT_OUTPUT;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        tag.putInt("Progress", this.progress);
        tag.putInt("MaxProgress", this.maxProgress);
        tag.putBoolean("IsCooking", this.isCooking);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        CompoundTag tag = packet.getTag();
        handleUpdateTag(tag, registries);
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        this.isCooking = tag.getBoolean("IsCooking");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        this.isCooking = tag.getBoolean("IsCooking");
    }

    @Override
    protected Component getDefaultName()  {
        return Component.translatable("container.oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new OvenGUIMenu(pContainerId, pPlayerInventory,
                new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory) {
        return createMenu(pContainerId, pPlayerInventory, null);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public boolean isCooking() {
        return this.isCooking;
    }

    public boolean isLit() {
        BlockState state = this.getBlockState();
        if(state.hasProperty(OvenBlock.LIT)) {
            return state.getValue(OvenBlock.LIT);
        }
        return false;
    }
}
