package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.ContainerRemainderHelper;
import net.mcreator.concoction.recipe.RecipeOutputData;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static java.lang.Math.min;

public class ButterChurnEntity extends AbstractSyncedContainerBlockEntity {
    private static final String CRAFT_RESULT_TAG = "churn.craft_result";

    private final int ContainerSize = 1;
    private RecipeHolder<ButterChurnRecipe> recipe = null;
    private RecipeOutputData craftResult = RecipeOutputData.EMPTY;

    private NonNullList<ItemStack> items = NonNullList.withSize(
            this.ContainerSize,
            ItemStack.EMPTY
    );

    public ButterChurnEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.BUTTER_CHURN.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.craftResult = RecipeOutputData.fromContainerTag(tag, CRAFT_RESULT_TAG);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    // Save values into the passed CompoundTag here.
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.craftResult.saveToContainerTag(tag, CRAFT_RESULT_TAG);

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
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
        stack.limitSize(min(this.getMaxStackSize(stack), 8));
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> Items) {
        this.items = Items;
        this.setChanged();
    }

    //Item add to container methods
    @SuppressWarnings("UnusedReturnValue")
    public boolean addItemOnClick(ItemStack addedItem, int count, boolean isCreative) {
        int toAdd = getAddableCount(addedItem, count);
        if (toAdd <= 0) {
            return false;
        }

        ItemStack currentStack = this.getItem(0);
        if (currentStack.isEmpty()) {
            this.setItem(0, addedItem.copyWithCount(toAdd));
        } else {
            currentStack.grow(toAdd);
            this.setChanged();
        }

        if (!isCreative) {
            addedItem.shrink(toAdd);
        }

        return true;
    }

    public ItemStack takeItemOnClick(boolean takeAll) {
        ItemStack returnStack = ItemStack.EMPTY;

        ItemStack itemstack = this.items.get(0);
        if (!itemstack.isEmpty()) {
            if (takeAll) {
                returnStack = itemstack.copy();
                this.setItem(0, ItemStack.EMPTY);
            } else {
                returnStack = itemstack.split(1);
            }
            this.setChanged();
            return returnStack;
        }

        return returnStack;
    }

    public void craftItem() {
        NonNullList<ItemStack> returned_items = checkReturnedItems();
        this.clearContent();
        if (!returned_items.stream().allMatch(ItemStack::isEmpty)) {
            this.setItems(returned_items);
        }
        this.craftResult = this.recipe.value().getOutput();
    }

    private NonNullList<ItemStack> checkReturnedItems() {
        NonNullList<ItemStack> returned_items = NonNullList.withSize(this.ContainerSize, ItemStack.EMPTY);
        ItemStack itemstack = this.items.getFirst();

        ItemStack returnedItem = ContainerRemainderHelper.getRemainder(itemstack, itemstack.getCount(), false);
        if (!returnedItem.isEmpty()) {
            returned_items.set(0, returnedItem);
        }
        return returned_items;
    }

    public boolean hasCraftedResult() {
        return !this.craftResult.isEmpty();
    }

    public RecipeOutputData getCraftResult() {
        return this.craftResult;
    }

    public void setCraftResult(RecipeOutputData result) {
        this.craftResult = result == null ? RecipeOutputData.EMPTY : result;
        this.setChanged();
    }

    public boolean hasRecipe() {
        Optional<RecipeHolder<ButterChurnRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        this.recipe = recipe.get();
        return true;
    }

    private Optional<RecipeHolder<ButterChurnRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ConcoctionModRecipes.BUTTER_CHURN_RECIPE_TYPE.get(),
                        new ButterChurnRecipeInput(this.getBlockState(), this.getItems()), level);
    }

    private int getAddableCount(ItemStack addedItem, int requestedCount) {
        if (addedItem.isEmpty() || requestedCount <= 0 || this.level == null || this.hasCraftedResult()) {
            return 0;
        }

        if (!isRecipeIngredient(addedItem)) {
            return 0;
        }

        ItemStack currentStack = this.getItem(0);
        if (!currentStack.isEmpty() && !ItemStack.isSameItemSameComponents(currentStack, addedItem)) {
            return 0;
        }

        int freeSpace = currentStack.isEmpty() ? 8 : 8 - currentStack.getCount();
        return Math.max(0, Math.min(requestedCount, freeSpace));
    }

    private boolean isRecipeIngredient(ItemStack stack) {
        return this.level.getRecipeManager()
                .getAllRecipesFor(ConcoctionModRecipes.BUTTER_CHURN_RECIPE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.getInputItems().stream().anyMatch(ingredient -> ingredient.test(stack)));
    }

    // Whether the container is considered "still valid" for the given player. For example, chests and
    // similar blocks check if the player is still within a given distance of the block here.
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // Clear the internal storage, setting all slots to empty again.
    @Override
    public void clearContent() {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.setChanged();
    }

    @Override
    protected void writeClientState(CompoundTag tag) {
    }

    @Override
    protected void readClientState(CompoundTag tag) {
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.butter_churn");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
        return null;
    }
}
