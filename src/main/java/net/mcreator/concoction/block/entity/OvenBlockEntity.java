package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.block.OvenBlock;
import net.mcreator.concoction.recipe.ContainerRemainderHelper;
import net.mcreator.concoction.recipe.RecipeHolderUtils;
import net.mcreator.concoction.recipe.RecipeOutputData;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipeInput;
import net.mcreator.concoction.world.inventory.OvenMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import org.jetbrains.annotations.Nullable;
import io.netty.buffer.Unpooled;

import java.util.*;

public class OvenBlockEntity extends AbstractSyncedContainerBlockEntity implements WorldlyContainer {
    private static final int CLIENT_SYNC_INTERVAL = 4;
    private static final String PROGRESS_TAG = "cooking.progress";
    private static final String MAX_PROGRESS_TAG = "cooking.max_progress";
    private static final String IS_COOKING_TAG = "cooking.is_cooking";

    // Слоты: 0 бутылочка, 1-6 крафт, 7 миска, 8 результат
    private final int ContainerSize = 9;
    private boolean isCooking = false;
    private RecipeHolder<OvenRecipe> recipe = null;

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

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt(PROGRESS_TAG);
        this.maxProgress = tag.getInt(MAX_PROGRESS_TAG);
        this.isCooking = tag.getBoolean(IS_COOKING_TAG);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(PROGRESS_TAG, this.progress);
        tag.putInt(MAX_PROGRESS_TAG, this.maxProgress);
        tag.putBoolean(IS_COOKING_TAG, this.isCooking);

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
        if (this.recipe == null || !RecipeHolderUtils.sameRecipe(rh, this.recipe, OvenRecipe::getIngredients)) {
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
        return RecipeHolderUtils.sameRecipe(recipe1, recipe2, OvenRecipe::getIngredients);

        // Сравниваем по ID рецептов (основной способ)

        // Если ID разные, сравниваем по ингредиентам


        // Создаем копии списков для сравнения

        // Сортируем по строковому представлению для сравнения

    }

    private boolean canAddResult() {
        if (recipe == null) return false;
        ItemStack resultStack = createResultStack(recipe);
        if (resultStack.isEmpty()) {
            return false;
        }

        ItemStack currentStack = items.get(SLOT_OUTPUT);
        if (currentStack.isEmpty()) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(resultStack, currentStack)
                && currentStack.getCount() + resultStack.getCount() <= currentStack.getMaxStackSize();


        // Проверяем, совпадает ли предмет в слоте результата с результатом рецепта

        // Проверяем, поместится ли результат
    }

    /** Сколько мисок нужно списывать за одну готовку (если миска требуется рецептом). */
    private int bowlsToConsumePerCraft(RecipeHolder<OvenRecipe> rh) {
        if (rh == null) return 0;
        if (rh.value().getBowlIngredient() == null || rh.value().getBowlIngredient().isEmpty()) return 0;
        return rh.value().getOutputCount();
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = recipe == null ? DEFAULT_MAX_PROGRESS : recipe.value().getCookingTime();
        this.isCooking = false;
        this.recipe = null;
        setChanged();
    }

    private void resetProgressOnly() {
        this.progress = 0;
        this.isCooking = false;
        setChanged();
    }

    private void craftItem() {
        if (recipe == null) return;

        ItemStack resultStack = createResultStack(recipe);
        if (resultStack.isEmpty()) {
            return;
        }

        ItemStack outputStack = items.get(SLOT_OUTPUT);
        if (outputStack.isEmpty()) {
            items.set(SLOT_OUTPUT, resultStack);
        } else if (ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            outputStack.grow(resultStack.getCount());
            items.set(SLOT_OUTPUT, outputStack);
        }

        // Добавляем в слот результата

        // Тратим ингредиенты (с учётом количества мисок = числу результата)
        consumeIngredients(recipe);

        setChanged();
    }

    private ItemStack createResultStack(RecipeHolder<OvenRecipe> recipeHolder) {
        if (recipeHolder == null) {
            return ItemStack.EMPTY;
        }

        RecipeOutputData result = recipeHolder.value().getResult();
        ItemStack resultStack = result.toStack();
        if (resultStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!resultStack.is(Items.SUSPICIOUS_STEW)) {
            return resultStack;
        }

        SuspiciousStewEffects suspiciousStewEffects = findSuspiciousStewEffects();
        if (suspiciousStewEffects == null || suspiciousStewEffects.equals(SuspiciousStewEffects.EMPTY)) {
            return ItemStack.EMPTY;
        }

        resultStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, suspiciousStewEffects);
        return resultStack;
    }

    @Nullable
    private SuspiciousStewEffects findSuspiciousStewEffects() {
        for (int slot = SLOT_FIRST_CRAFT; slot <= SLOT_LAST_CRAFT; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) {
                continue;
            }

            SuspiciousEffectHolder holder = SuspiciousEffectHolder.tryGet(stack.getItem());
            if (holder != null) {
                return holder.getSuspiciousEffects();
            }
        }

        return null;
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
                ItemStack drop = ContainerRemainderHelper.getRemainder(stack, toRemove, true);
                if (!drop.isEmpty()) {
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
        markChangedLocal();
        if (this.progress == 1 || this.progress >= this.maxProgress || this.progress % CLIENT_SYNC_INTERVAL == 0) {
            syncClientState();
        }
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
                if (originalRecipe.isPresent() && RecipeHolderUtils.sameRecipe(originalRecipe.get(), this.recipe, OvenRecipe::getIngredients)) {
                    shouldReset = false;
                } else if (newRecipe.isPresent()) {
                    if (!RecipeHolderUtils.sameRecipe(newRecipe.get(), this.recipe, OvenRecipe::getIngredients)) {
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
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.setChanged();
    }

    @Override
    protected void writeClientState(CompoundTag tag) {
        tag.putInt("Progress", this.progress);
        tag.putInt("MaxProgress", this.maxProgress);
        tag.putBoolean("IsCooking", this.isCooking);
    }

    @Override
    protected void readClientState(CompoundTag tag) {
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
        return new OvenMenu(pContainerId, pPlayerInventory,
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
