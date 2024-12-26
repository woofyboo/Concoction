package net.mcreator.concoction.block.entity;

import com.google.gson.Gson;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipeInput;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.min;

public class ButterChurnEntity extends RandomizableContainerBlockEntity {
    private final int ContainerSize = 1;
    private RecipeHolder<ButterChurnRecipe> recipe = null;
    private Map<String, String> craftResult = Map.ofEntries(
            Map.entry("id",""),
            Map.entry("count",""),
            Map.entry("interactionType","")
    );

    private NonNullList<ItemStack> items = NonNullList.withSize(
            this.ContainerSize,
            ItemStack.EMPTY
    );

    public ButterChurnEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.BUTTER_CHURN.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.craftResult = (new Gson()).fromJson(tag.getString("churn.craft_result"), HashMap.class);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    // Save values into the passed CompoundTag here.
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("churn.craft_result", (new Gson()).toJson(this.craftResult));

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
        boolean flag = false;
        ItemStack itemstack = this.getItem(0);
        if (itemstack.isEmpty()) {
            this.setItem(0, isCreative ? addedItem.copyWithCount(1) : addedItem.split(count));
            flag = true;
        }

        else if (!itemstack.getItem().equals(addedItem.getItem()))
            flag = false;

        else if (itemstack.getItem().equals(addedItem.getItem()) && itemstack.getMaxStackSize() > itemstack.getCount()+count) {
            int to_add = min(count, 8-itemstack.getCount());
            if (to_add == 0) return false;
            if (isCreative || addedItem.getCount() - addedItem.split(to_add).getCount() != 0) {
                this.items.getFirst().grow(to_add);
                this.setChanged();
                flag = true;
            }
        }
//        if (flag) this.resetProgress();
        return flag;
    }

    public ItemStack takeItemOnClick(boolean takeAll) {
//        if (this.isCooking) return ItemStack.EMPTY;
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
//                this.resetProgress();
            return returnStack;
        }

        return returnStack;
    }

    public void craftItem() {
        NonNullList<ItemStack> returned_items = checkReturnedItems();
        this.clearContent();
        this.setItems(returned_items);
        this.craftResult = this.recipe.value().getOutput();

//      this.setItem(0, this.output);
    }

    private NonNullList<ItemStack> checkReturnedItems() {
        NonNullList<ItemStack> returned_items = NonNullList.withSize(this.ContainerSize, ItemStack.EMPTY);
        for (int i=0; i < this.ContainerSize; i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.is(ItemTags.create(ResourceLocation.parse("c:buckets"))))
                returned_items.set(i,  new ItemStack(Items.BUCKET));
            else if (itemstack.is(ItemTags.create(ResourceLocation.parse("c:bottles"))))
                returned_items.set(i,  new ItemStack(Items.GLASS_BOTTLE));
//            else if (itemstack.getItem().equals(Items.IRON_AXE)) {
//                ItemStack axe = new ItemStack(Items.IRON_AXE);
//                axe.setDamageValue(( (int) (itemstack.getDamageValue()*0.8f)));
//                returned_items.set(i, axe);
//            }
        }
        return returned_items;
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

    // Whether the container is considered "still valid" for the given player. For example, chests and
    // similar blocks check if the player is still within a given distance of the block here.
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // Clear the internal storage, setting all slots to empty again.
    @Override
    public void clearContent() {
        items.clear();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // This will send the block entity data to the client every time the block entity is marked as changed.
        // This is useful for syncing data between the server and client.
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Optionally: Run some custom logic when the packet is received.
    // The super/default implementation forwards to #loadAdditional.
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        // Do whatever you need to do here.
    }

    // Handle a received update tag here. The default implementation calls #loadAdditional here,
    // so you do not need to override this method if you don't plan to do anything beyond that.
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
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
