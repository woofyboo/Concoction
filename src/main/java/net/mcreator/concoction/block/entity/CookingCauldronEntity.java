package net.mcreator.concoction.block.entity;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import net.mcreator.concoction.block.CookingCauldron;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipeInput;
import net.mcreator.concoction.world.inventory.BoilingCauldronInterfaceMenu;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.*;

public class CookingCauldronEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    // Слоты: 0-3 ингредиенты, 4 "миска"/половник, 5 результат
    private final int ContainerSize = 6;
    private boolean isCooking = false;
    private RecipeHolder<CauldronBrewingRecipe> recipe = null;
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

    // Индексы слотов
    private static final int SLOT_INGREDIENT_FIRST = 0; // 0..3
    private static final int SLOT_INGREDIENT_LAST = 3;
    private static final int SLOT_BOWL = 4;
    private static final int SLOT_OUTPUT = 5;

    // Массивы слотов для удобства
    private static final int[] SLOTS_INGREDIENTS = new int[]{
            0, 1, 2, 3
    };
    private static final int[] SLOTS_OUTPUT_ONLY = new int[]{
            SLOT_OUTPUT
    };
    private static final int[] SLOTS_BOWL_ONLY = new int[]{
            SLOT_BOWL
    };

    public CookingCauldronEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.COOKING_CAULDRON.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("cooking.progress");
        this.maxProgress = tag.getInt("cooking.max_progress");
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
        tag.putString("cooking.craft_result", (new Gson()).toJson(this.craftResult));

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos) {
        if (fromPos.equals(pos.below())) {
            checkHeatSource(level, pos, state);
        }
    }

    private void checkHeatSource(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        Block blockBelow = level.getBlockState(pos.below()).getBlock();
        boolean hasHeatSource = level.getFluidState(pos.below()).is(Fluids.LAVA.getSource()) ||
                blockBelow instanceof FireBlock ||
                blockBelow instanceof MagmaBlock ||
                blockBelow instanceof CampfireBlock;

        if (!hasHeatSource) {
            if (state.getValue(CookingCauldron.LIT) || state.getValue(CookingCauldron.COOKING)) {
                BlockState defaultState = state.getBlock().defaultBlockState()
                        .setValue(CookingCauldron.LEVEL, state.getValue(CookingCauldron.LEVEL));

                level.setBlock(pos, defaultState, 3);
                resetProgress();
            }
        } else if (!state.getValue(CookingCauldron.LIT)) {
            level.setBlock(pos, state.setValue(CookingCauldron.LIT, true), 3);
        }
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        if (!level.isClientSide) {
            checkHeatSource(level, pPos, pState);

            if (pState.getValue(CookingCauldron.LIT)) {
                if (this.isCooking) {
                    if (!validateCurrentRecipe()) {
                        level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                        resetProgress();
                        return;
                    }

                    if (this.recipe != null) {
                        ItemStack resultSlot = this.items.get(SLOT_OUTPUT);
                        if (!resultSlot.isEmpty()) {
                            String recipeResultId = this.recipe.value().getOutput().get("id");
                            if (!recipeResultId.isEmpty()) {
                                ResourceLocation recipeItemId = ResourceLocation.parse(recipeResultId);
                                ResourceLocation currentItemId = BuiltInRegistries.ITEM.getKey(resultSlot.getItem());

                                if (!currentItemId.equals(recipeItemId)) {
                                    level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                                    resetProgress();
                                    return;
                                }

                                int currentCount = resultSlot.getCount();
                                int recipeCount = Integer.parseInt(this.recipe.value().getOutput().get("count"));
                                if (currentCount + recipeCount > resultSlot.getMaxStackSize()) {
                                    level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                                    resetProgressOnly();
                                    return;
                                }
                            }
                        }
                    }

                    increaseCraftingProgress();
                    setChanged(level, pPos, pState);

                    if (hasCraftingFinished()) {
                        craftItem();
                        level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                    }
                } else if (!hasCraftedResult() && hasRecipe()) {
                    level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, true));
                    this.isCooking = true;
                    setChanged(level, pPos, pState);
                }
            } else {
                level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                resetProgress();
            }
        }
    }

    private boolean validateCurrentRecipe() {
        if (this.recipe == null) return false;

        String interactionType = this.recipe.value().getOutput().get("interactionType");
        if (!interactionType.equals("hand")) {
            ItemStack ladleStack = this.items.get(4);
            if (ladleStack.isEmpty()) {
                return false;
            }
        }

        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : this.recipe.value().getInputItems()) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }

        for (ItemStack itemStack : this.items.subList(0, 4)) {
            if (itemStack.isEmpty()) continue;

            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(itemStack)) {
                    entry.setValue(entry.getValue() - 1);
                    break;
                }
            }
        }

        return requiredIngredients.values().stream().allMatch(count -> count <= 0);
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
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : this.recipe.value().getInputItems()) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }

        NonNullList<ItemStack> newItems = NonNullList.withSize(this.ContainerSize, ItemStack.EMPTY);
        List<ItemStack> itemsToSpawn = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = this.items.get(i);
            if (itemStack.isEmpty()) continue;

            boolean used = false;
            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(itemStack)) {
                    entry.setValue(entry.getValue() - 1);
                    used = true;

                    if (itemStack.getCount() > 1) {
                        if (itemStack.is(ItemTags.create(ResourceLocation.parse("c:buckets")))) {
                            itemsToSpawn.add(new ItemStack(Items.BUCKET));
                        } else if (itemStack.is(ItemTags.create(ResourceLocation.parse("c:bottles")))) {
                            itemsToSpawn.add(new ItemStack(Items.GLASS_BOTTLE));
                        }
                    }
                    break;
                }
            }

            if (!used || itemStack.getCount() > 1) {
                ItemStack remainingStack = itemStack.copy();
                if (used) {
                    remainingStack.shrink(1);
                }
                newItems.set(i, remainingStack);
            }
        }

        String interactionType = this.recipe.value().getOutput().get("interactionType");

        if (!interactionType.equals("hand")) {
            ItemStack ladleStack = this.items.get(4);
            if (!ladleStack.isEmpty()) {
                boolean isCorrectLadleItem = switch (interactionType) {
                    case "bottle" -> ladleStack.getItem() == Items.GLASS_BOTTLE;
                    case "stick" -> ladleStack.getItem() == Items.STICK;
                    case "bucket" -> ladleStack.getItem() == Items.BUCKET;
                    case "bowl" -> ladleStack.getItem() == Items.BOWL;
                    default -> false;
                };

                if (isCorrectLadleItem) {
                    if (ladleStack.getCount() > 1) {
                        ItemStack remainingLadle = ladleStack.copy();
                        remainingLadle.shrink(1);
                        newItems.set(4, remainingLadle);
                    }
                }
            }
        } else {
            newItems.set(4, this.items.get(4).copy());
        }

        this.craftResult = this.recipe.value().getOutput();

        if (!this.craftResult.get("id").isEmpty()) {
            ResourceLocation itemId = ResourceLocation.parse(this.craftResult.get("id"));
            int craftedCount = Integer.parseInt(this.craftResult.get("count"));

            ItemStack currentResult = this.items.get(SLOT_OUTPUT);
            ItemStack newResult;

            if (!currentResult.isEmpty() && currentResult.is(BuiltInRegistries.ITEM.get(itemId))) {
                newResult = currentResult.copy();
                int spaceLeft = newResult.getMaxStackSize() - newResult.getCount();
                int toAdd = Math.min(craftedCount, spaceLeft);
                if (toAdd > 0) {
                    newResult.grow(toAdd);
                }
            } else {
                newResult = new ItemStack(BuiltInRegistries.ITEM.get(itemId), craftedCount);
            }

            newItems.set(SLOT_OUTPUT, newResult);
        }

        this.setItems(newItems);

        if (!itemsToSpawn.isEmpty() && this.level != null) {
            for (ItemStack stack : itemsToSpawn) {
                Block.popResource(this.level, this.worldPosition, stack);
            }
        }

        if (hasRecipe()) {
            resetProgressOnly();
        } else {
            resetProgress();
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
        setChanged();
    }

    public boolean hasCraftedResult() {
        return !this.craftResult.get("id").isEmpty();
    }

    public Map<String, String> getCraftResult() {
        return this.craftResult;
    }

    public void setCraftResult(Map<String, String> result) {
        this.craftResult = result;
        this.setChanged();
    }

    private boolean isSameRecipe(RecipeHolder<CauldronBrewingRecipe> recipe1, RecipeHolder<CauldronBrewingRecipe> recipe2) {
        if (recipe1 == null || recipe2 == null) return false;

        if (recipe1.id().equals(recipe2.id())) return true;

        List<Ingredient> ingredients1 = recipe1.value().getInputItems();
        List<Ingredient> ingredients2 = recipe2.value().getInputItems();

        if (ingredients1.size() != ingredients2.size()) return false;

        List<Ingredient> sorted1 = new ArrayList<>(ingredients1);
        List<Ingredient> sorted2 = new ArrayList<>(ingredients2);

        sorted1.sort((a, b) -> a.toString().compareTo(b.toString()));
        sorted2.sort((a, b) -> a.toString().compareTo(b.toString()));

        for (int i = 0; i < sorted1.size(); i++) {
            if (!sorted1.get(i).toString().equals(sorted2.get(i).toString())) {
                return false;
            }
        }

        return true;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<CauldronBrewingRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        RecipeHolder<CauldronBrewingRecipe> newRecipe = recipe.get();

        if (this.recipe != null && !isSameRecipe(this.recipe, newRecipe)) {
            this.resetProgress();
            return false;
        }

        ItemStack resultSlot = this.items.get(SLOT_OUTPUT);
        if (!resultSlot.isEmpty()) {
            String recipeResultId = newRecipe.value().getOutput().get("id");
            if (!recipeResultId.isEmpty()) {
                ResourceLocation recipeItemId = ResourceLocation.parse(recipeResultId);
                ResourceLocation currentItemId = BuiltInRegistries.ITEM.getKey(resultSlot.getItem());

                if (!currentItemId.equals(recipeItemId)) {
                    return false;
                }

                int currentCount = resultSlot.getCount();
                int recipeCount = Integer.parseInt(newRecipe.value().getOutput().get("count"));
                if (currentCount + recipeCount > resultSlot.getMaxStackSize()) {
                    return false;
                }
            }
        }

        String interactionType = newRecipe.value().getOutput().get("interactionType");
        ItemStack ladleItem = this.items.get(4);

        if (interactionType.equals("hand")) {
            this.recipe = newRecipe;
            this.maxProgress = newRecipe.value().getCookingTime();
            return true;
        }

        boolean hasCorrectLadle = switch (interactionType) {
            case "bottle" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.GLASS_BOTTLE;
            case "stick" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.STICK;
            case "bucket" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.BUCKET;
            case "bowl" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.BOWL;
            default -> false;
        };

        if (hasCorrectLadle) {
            this.recipe = newRecipe;
            this.maxProgress = newRecipe.value().getCookingTime();
            return true;
        }

        return false;
    }

    private Optional<RecipeHolder<CauldronBrewingRecipe>> getCurrentRecipe() {
        if (isCooking) return Optional.empty();
        return this.level.getRecipeManager()
                .getRecipeFor(ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_TYPE.get(),
                        new CauldronBrewingRecipeInput(this.getBlockState(), this.getItems()), level);
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

        boolean isIngredientSlot = slot >= 0 && slot < 4;
        boolean isLadleSlot = slot == 4;

        if ((isIngredientSlot || isLadleSlot) && (isDifferentItem || isSlotEmpty != isStackEmpty)) {

            ItemStack oldStack = this.items.get(slot);

            ItemStack testStack = oldStack.copy();
            if (!testStack.isEmpty()) {
                testStack.setCount(1);
            }
            this.items.set(slot, testStack);

            Optional<RecipeHolder<CauldronBrewingRecipe>> originalRecipe = getCurrentRecipe();

            this.items.set(slot, stack);
            Optional<RecipeHolder<CauldronBrewingRecipe>> newRecipe = getCurrentRecipe();

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

                if (this.level != null) {
                    BlockState state = this.level.getBlockState(this.worldPosition);
                    if (state.hasProperty(CookingCauldron.COOKING)) {
                        this.level.setBlock(this.worldPosition,
                                state.setValue(CookingCauldron.COOKING, false),
                                3);
                    }
                }
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

    // ----- WorldlyContainer: хопперы / автология -----

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            // сверху — только 4 слота ингредиентов
            return SLOTS_INGREDIENTS;
        }
        if (side == Direction.DOWN) {
            // снизу — только результат
            return SLOTS_OUTPUT_ONLY;
        }
        // любые горизонтальные стороны — только слот "миски"/посуды
        return SLOTS_BOWL_ONLY;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction side) {
        if (side == null) return false;

        if (side == Direction.UP) {
            // сверху можно класть только в слоты 0..3
            return index >= SLOT_INGREDIENT_FIRST && index <= SLOT_INGREDIENT_LAST;
        }

        if (side == Direction.DOWN) {
            // снизу ничего не кладём
            return false;
        }

        // горизонтальные стороны → только в слот миски, и только если предмет из #c:tableware
        if (index == SLOT_BOWL) {
            return stack.is(ItemTags.create(ResourceLocation.parse("c:tableware")));
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side) {
        // снизу хоппер забирает только результат
        if (side == Direction.DOWN) {
            return index == SLOT_OUTPUT;
        }
        // с других сторон ничего не выдаём
        return false;
    }

    // ----- остальное без изменений -----

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
    protected Component getDefaultName() {
        return Component.translatable("container.cooking_cauldron");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BoilingCauldronInterfaceMenu(pContainerId, pPlayerInventory,
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
        if(state.hasProperty(CookingCauldron.LIT)) {
            return state.getValue(CookingCauldron.LIT);
        }
        return false;
    }
}
