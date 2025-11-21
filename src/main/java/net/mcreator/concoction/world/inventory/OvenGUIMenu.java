package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.block.entity.OvenBlockEntity;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipeInput;
import net.mcreator.concoction.utils.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class OvenGUIMenu
        extends RecipeBookMenu<OvenRecipeInput, OvenRecipe>
        implements Supplier<Map<Integer, Slot>> {

    public static final HashMap<String, Object> guistate = new HashMap<>();

    public final Level world;
    public final Player entity;
    public int x, y, z;
    private BlockPos pos;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private IItemHandler internal;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;
    private Supplier<Boolean> boundItemMatcher = null;
    private Entity boundEntity = null;
    private BlockEntity boundBlockEntity = null;

    private int progress = 0;
    private int maxProgress = 200;
    private boolean isCooking = false;
    private boolean isLit = false;

    private ResourceLocation lastRecipeId = null;

    public OvenGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ConcoctionModMenus.OVEN_GUI.get(), id);

        this.entity = inv.player;
        this.world = inv.player.level();

        if (extraData != null) {
            this.pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.access = ContainerLevelAccess.create(world, pos);

            world.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    ConcoctionModSounds.OVEN_OPEN.get(),
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0F,
                    false
            );
        }

        // SERVER: выдаём рецепты духовки
        if (!world.isClientSide && this.entity instanceof ServerPlayer serverPlayer) {
            var ovenType = ConcoctionModRecipes.OVEN_RECIPE_TYPE.get();
            var mgr = serverPlayer.server.getRecipeManager();
            var ovenRecipes = mgr.getAllRecipesFor(ovenType);
            serverPlayer.awardRecipes((java.util.Collection) ovenRecipes);
        }

        // === bind к BlockEntity ===
        if (this.pos != null && world.getBlockEntity(this.pos) instanceof OvenBlockEntity blockEntity) {
            this.boundBlockEntity = blockEntity;
            this.bound = true;

            this.internal = new ItemStackHandler(9) {

                @Override
                public ItemStack getStackInSlot(int slot) {
                    return blockEntity.getItem(slot);
                }

                @Override
                public void setStackInSlot(int slot, ItemStack stack) {
                    blockEntity.setItem(slot, stack);
                }

                @Override
                public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                    if (!simulate) blockEntity.setItem(slot, stack);
                    return ItemStack.EMPTY;
                }

                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    ItemStack existing = blockEntity.getItem(slot);
                    if (existing.isEmpty()) return ItemStack.EMPTY;

                    ItemStack extracted = existing.copy();
                    extracted.setCount(Math.min(amount, existing.getCount()));

                    if (!simulate) {
                        existing.shrink(amount);
                        if (existing.isEmpty()) blockEntity.setItem(slot, ItemStack.EMPTY);
                    }
                    return extracted;
                }
            };
        } else {
            this.internal = new ItemStackHandler(9);
        }

        // ==== игрок ====
        addPlayerHotbar(inv);
        addPlayerInventory(inv);

        // ==== Слоты духовки ====
        customSlots.put(36, this.addSlot(new OvenBottleSlot(internal, 0, 19, 33)));

        customSlots.put(37, this.addSlot(new SlotItemHandler(internal, 1, 41, 24)));
        customSlots.put(38, this.addSlot(new SlotItemHandler(internal, 2, 59, 24)));
        customSlots.put(39, this.addSlot(new SlotItemHandler(internal, 3, 77, 24)));
        customSlots.put(40, this.addSlot(new SlotItemHandler(internal, 4, 41, 42)));
        customSlots.put(41, this.addSlot(new SlotItemHandler(internal, 5, 59, 42)));
        customSlots.put(42, this.addSlot(new SlotItemHandler(internal, 6, 77, 42)));

        customSlots.put(43, this.addSlot(new OvenBowlSlot(internal, 7, 104, 13)));

        customSlots.put(44, this.addSlot(new SlotItemHandler(internal, 8, 139, 34) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }

            @Override public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if (!player.level().isClientSide() && player instanceof ServerPlayer sp) {
                    Utils.addAchievement(sp, "concoction:oven_cooking");
                }
            }
        }));
    }

    // ========= GUI state =========

    public int getProgress() {
        return boundBlockEntity instanceof OvenBlockEntity be ? be.getProgress() : progress;
    }

    public int getMaxProgress() {
        return boundBlockEntity instanceof OvenBlockEntity be ? be.getMaxProgress() : maxProgress;
    }

    public boolean isCooking() {
        return boundBlockEntity instanceof OvenBlockEntity be ? be.isCooking() : isCooking;
    }

    public boolean isLit() {
        return boundBlockEntity instanceof OvenBlockEntity be ? be.isLit() : isLit;
    }

    // ========= RecipeBookMenu impl =========

    @Override
    public void fillCraftSlotsStackedContents(StackedContents contents) {
        for (int idx = 37; idx <= 42; idx++) {
            if (idx < this.slots.size()) {
                Slot s = this.slots.get(idx);
                if (s != null && s.hasItem()) contents.accountSimpleStack(s.getItem());
            }
        }
    }

    @Override public void clearCraftingContent() {}

    @Override
    public boolean recipeMatches(RecipeHolder<OvenRecipe> recipeHolder) {
        return recipeHolder != null && recipeHolder.value() != null;
    }

    @Override
    public int getResultSlotIndex() { return 44; }

    @Override public int getGridWidth() { return 3; }
    @Override public int getGridHeight() { return 2; }
    @Override public int getSize() { return this.slots.size(); }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.FURNACE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return List.of(RecipeBookCategories.FURNACE_FOOD);
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex == 44;
    }

    // ========= Автосборка ==========

    @Override
    public void handlePlacement(boolean placeAll, RecipeHolder<?> holder, ServerPlayer player) {
        if (holder == null || holder.value() == null) return;
        if (!(holder.value() instanceof OvenRecipe ovenRecipe)) return;
        if (ovenRecipe.getType() != ConcoctionModRecipes.OVEN_RECIPE_TYPE.get()) return;

        ResourceLocation id = holder.id();
        boolean recipeChanged = lastRecipeId == null || !lastRecipeId.equals(id);

        if (recipeChanged) {
            returnOvenInputsToPlayer(player);
        }

        List<Ingredient> craftIngs = ovenRecipe.getCraftingIngredients();
        Ingredient bottleIng = ovenRecipe.getBottleIngredient();
        Ingredient bowlIng = ovenRecipe.getBowlIngredient();
        int bowlCost = ovenRecipe.getBowlCost();

        int existing = getExistingPatterns(craftIngs, bowlIng, bowlCost);

        if (placeAll) {
            int p = existing;
            while (placePatternToReach(craftIngs, bottleIng, bowlIng, bowlCost, p + 1)) {
                p++;
            }
        } else {
            placePatternToReach(craftIngs, bottleIng, bowlIng, bowlCost, existing + 1);
        }

        lastRecipeId = id;
    }

    private int getExistingPatterns(List<Ingredient> craftIngs, Ingredient bowlIng, int bowlCost) {
        int fromBowls = Integer.MAX_VALUE;
        if (!bowlIng.isEmpty() && bowlCost > 0) {
            Slot bowlSlot = this.slots.get(43);
            ItemStack st = bowlSlot.getItem();
            if (!st.isEmpty() && bowlIng.test(st))
                fromBowls = st.getCount() / bowlCost;
            else
                fromBowls = 0;
        }

        int fromGrid = Integer.MAX_VALUE;
        if (!craftIngs.isEmpty()) {
            Slot firstGrid = this.slots.get(37);
            ItemStack st = firstGrid.getItem();
            Ingredient firstIng = craftIngs.get(0);

            if (!st.isEmpty() && firstIng.test(st))
                fromGrid = st.getCount();
            else
                fromGrid = 0;
        }

        int p = Math.min(fromBowls, fromGrid);
        return p == Integer.MAX_VALUE ? 0 : p;
    }

    private boolean placePatternToReach(List<Ingredient> craftIngs,
                                        Ingredient bottleIng,
                                        Ingredient bowlIng,
                                        int bowlCost,
                                        int targetPatterns) {

        if (!bottleIng.isEmpty()) {
            if (!ensureCountInSlot(bottleIng, 36, targetPatterns)) return false;
        }

        if (!bowlIng.isEmpty() && bowlCost > 0) {
            if (!ensureCountInSlot(bowlIng, 43, targetPatterns * bowlCost)) return false;
        }

        int[] grid = {37, 38, 39, 40, 41, 42};
        int max = Math.min(craftIngs.size(), grid.length);
        for (int i = 0; i < max; i++) {
            Ingredient ing = craftIngs.get(i);
            if (!ing.isEmpty()) {
                if (!ensureCountInSlot(ing, grid[i], targetPatterns)) return false;
            }
        }

        return true;
    }

    private boolean ensureCountInSlot(Ingredient ingredient, int targetSlotIndex, int required) {
        if (required <= 0) return true;
        if (targetSlotIndex >= this.slots.size()) return false;

        Slot target = this.slots.get(targetSlotIndex);
        ItemStack current = target.getItem();
        int have = 0;

        if (!current.isEmpty()) {
            if (!ingredient.test(current)) return false;
            have = current.getCount();
        }

        while (have < required) {
            ItemStack taken = takeOneMatchingFromPlayer(ingredient);
            if (taken.isEmpty()) break;
            if (current.isEmpty()) current = taken;
            else current.grow(1);
            have++;
        }

        if (current.isEmpty()) target.set(ItemStack.EMPTY);
        else target.set(current);
        target.setChanged();

        return have >= required;
    }

    private ItemStack takeOneMatchingFromPlayer(Ingredient ingredient) {
        for (int i = 0; i < 36 && i < this.slots.size(); i++) {
            Slot invSlot = this.slots.get(i);
            if (invSlot == null) continue;

            ItemStack st = invSlot.getItem();
            if (!st.isEmpty() && ingredient.test(st)) {
                ItemStack result = st.split(1);
                if (st.isEmpty()) invSlot.set(ItemStack.EMPTY);
                invSlot.setChanged();
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    private void returnOvenInputsToPlayer(ServerPlayer player) {
        int[] slots = {36, 37, 38, 39, 40, 41, 42, 43};

        for (int idx : slots) {
            if (idx >= this.slots.size()) continue;

            Slot slot = this.slots.get(idx);
            if (slot == null) continue;

            ItemStack st = slot.getItem();
            if (st.isEmpty()) continue;

            if (!this.moveItemStackTo(st, 0, 36, false)) {
                player.drop(st, false);
            }

            slot.set(ItemStack.EMPTY);
            slot.setChanged();
        }
    }

    // ====== Menu logic ======

    @Override
    public boolean stillValid(Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null) return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return RecipeBookMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null) return this.boundEntity.isAlive();
        }
        return true;
    }

    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < 3; ++i)
            for (int l = 0; l < 9; ++l)
                this.addSlot(new Slot(inv, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index >= 36 && index <= 44) {
                if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 9, false))
                        return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            else if (index >= 0 && index < 36) {
                boolean moved = false;

                if (itemstack1.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tableware")))) {
                    if (this.moveItemStackTo(itemstack1, 43, 44, false)) moved = true;
                }

                if (!moved) {
                    if (this.moveItemStackTo(itemstack1, 36, 37, false)) moved = true;
                    else if (this.moveItemStackTo(itemstack1, 37, 43, false)) moved = true;
                }

                if (!moved) return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide && player instanceof ServerPlayer sp) {
            if (this.pos != null && !sp.hasDisconnected()) {
                player.level().playSound(
                        null,
                        this.pos,
                        ConcoctionModSounds.OVEN_CLOSE.get(),
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F
                );
            }
        }
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }
}
