package net.mcreator.concoction.block.entity;

import com.mojang.datafixers.kinds.IdF;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

import static net.minecraft.world.level.block.entity.HopperBlockEntity.suckInItems;

public class CookingCauldronEntity extends RandomizableContainerBlockEntity implements Hopper {
    // This can be any value of any type you want, so long as you can somehow serialize it to NBT.
    // We will use an int for the sake of example.
    // Container methods and fields
    private final int ContainerSize = 6;
    private int cooldownTime = -1;
    private NonNullList<ItemStack> items = NonNullList.withSize(
            this.ContainerSize,
            ItemStack.EMPTY
    );


    public CookingCauldronEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.COOKING_CAULDRON.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    // Save values into the passed CompoundTag here.
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
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
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
        this.setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(this.items, slot);
        this.setChanged();
        return stack;
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
        stack.limitSize(this.getMaxStackSize(stack));
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> Items) {
        this.items = Items;
    }

    //Hopper block entity methods
    // The signature of this method matches the signature of the BlockEntityTicker functional interface.
    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T blockEntity) {
        if (!level.isClientSide && blockEntity instanceof CookingCauldronEntity cauldronEntity) {
            tryMoveItems(level, blockPos, blockState, cauldronEntity, () -> suckInItems(level, cauldronEntity));

//            if (!this.level().isClientSide && this.isAlive() && this.isEnabled() && this.suckInItems()) {
//                this.setChanged();
//            }
//            if (!cauldronEntity.isOnCooldown()) {
//                cauldronEntity.setCooldown(8);
//                HopperBlockEntity.tryMoveInItems(level, blockPos, blockState);
//            }
        }
    }

    private static boolean tryMoveItems(Level p_155579_, BlockPos p_155580_, BlockState p_155581_, CookingCauldronEntity p_155582_, BooleanSupplier p_155583_) {
        if (!p_155582_.isOnCooldown()) {
            boolean flag = false;
//            if (!p_155582_.isEmpty()) {
//                flag = ejectItems(p_155579_, p_155580_, p_155582_);
//            }

            if (!p_155582_.inventoryFull()) {
                flag |= p_155583_.getAsBoolean();
            }

            if (flag) {
                p_155582_.setCooldown(8);
                setChanged(p_155579_, p_155580_, p_155581_);
                return true;
            }
        }
        return false;

    }
    private boolean inventoryFull() {
        for (ItemStack itemstack : this.items) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public boolean addItemOnClick(ItemStack addedItem, int count, boolean isCreative) {
        boolean flag = false;
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.items.set(i, isCreative ? addedItem.copyWithCount(1) : addedItem.split(count));
                this.setChanged();
                flag = true;
                break;
//            } else if ( itemstack.getItem().equals(addedItem.getItem()) ) {
//                int to_add = Math.min(count, itemstack.getMaxStackSize()-itemstack.getCount());
//                if (isCreative || addedItem.getCount() - addedItem.split(to_add).getCount() != 0) {
//                    this.items.get(i).grow(to_add);
//                    this.setChanged();
//                    flag = true;
//                    break;
//                }
            }
        }
        return flag;
    }

    public ItemStack takeItemOnClick(boolean takeAll) {
        ItemStack returnStack = ItemStack.EMPTY;
        for (int i = this.items.size()-1; i >= 0; i--) {
            ItemStack itemstack = this.items.get(i);
            if (!itemstack.isEmpty()) {
                if (takeAll) {
                    returnStack = itemstack.copy();
                    this.items.set(i, ItemStack.EMPTY);
                } else {
                    returnStack = itemstack.split(1);
                }
                this.setChanged();
                return returnStack;
            }
        }
        return returnStack;
    }

//    public boolean addItem(Container container, ItemEntity itemEntity) {
//        boolean flag = false;
//        ItemStack itemstack = itemEntity.getItem().copy();
//        ItemStack itemstack1 = addItem(container, itemstack);
//        if (itemstack1.isEmpty()) {
//            flag = true;
//            itemEntity.setItem(ItemStack.EMPTY);
//            itemEntity.discard();
//        } else itemEntity.setItem(itemstack1);
//        return flag;
//    }


    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    private void setCooldown(int cooldownTime) {
        this.cooldownTime = cooldownTime;
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
        return Component.translatable("container.cooking_cauldron");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
        return new HopperMenu(p_58627_, p_58628_, this);
    }

    // Hopper methods
    @Override
    public double getLevelX() {
        return (double)this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return (double)this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return (double)this.worldPosition.getZ() + 0.5;
    }

    @Override
    public boolean isGridAligned() {
        return true;
    }
}
