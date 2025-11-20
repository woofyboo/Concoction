package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.block.entity.OvenBlockEntity;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.utils.Utils;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipeInput;

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

    // локальный кэш на всякий случай
    private int progress = 0;
    private int maxProgress = 200;
    private boolean isCooking = false;
    private boolean isLit = false;

    public OvenGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ConcoctionModMenus.OVEN_GUI.get(), id);

        this.entity = inv.player;
        this.world = inv.player.level();
        this.pos = null;

        if (!this.world.isClientSide && this.entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            var ovenType = ConcoctionModRecipes.OVEN_RECIPE_TYPE.get();
            var mgr = serverPlayer.server.getRecipeManager();

            // все рецепты типа OVEN
            var ovenRecipes = mgr.getAllRecipesFor(ovenType);

            // грязный, но рабочий upcast в Collection<RecipeHolder<?>>
            serverPlayer.awardRecipes((java.util.Collection) ovenRecipes);
        }


        if (extraData != null) {
            this.pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.access = ContainerLevelAccess.create(world, pos);

            // звук открытия духовки
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

        if (world.getBlockEntity(pos) instanceof OvenBlockEntity blockEntity) {
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
                    if (!simulate) {
                        blockEntity.setItem(slot, stack);
                    }
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
                        if (existing.isEmpty()) {
                            blockEntity.setItem(slot, ItemStack.EMPTY);
                        }
                    }

                    return extracted;
                }
            };
        } else {
            this.internal = new ItemStackHandler(9);
        }

        // ——— слоты игрока ———
        addPlayerHotbar(inv);
        addPlayerInventory(inv);

        // === СЛОТЫ ДУХОВКИ (как были, индексы не трогаем) ===
        // 0 — бутылочка / соус
        this.customSlots.put(36, this.addSlot(new OvenBottleSlot(internal, 0, 19, 33)));

        // 1..6 — 3×2 сетка ингредиентов
        this.customSlots.put(37, this.addSlot(new SlotItemHandler(internal, 1, 41, 24)));
        this.customSlots.put(38, this.addSlot(new SlotItemHandler(internal, 2, 59, 24)));
        this.customSlots.put(39, this.addSlot(new SlotItemHandler(internal, 3, 77, 24)));
        this.customSlots.put(40, this.addSlot(new SlotItemHandler(internal, 4, 41, 42)));
        this.customSlots.put(41, this.addSlot(new SlotItemHandler(internal, 5, 59, 42)));
        this.customSlots.put(42, this.addSlot(new SlotItemHandler(internal, 6, 77, 42)));

        // 7 — миска / посуда
        this.customSlots.put(43, this.addSlot(new OvenBowlSlot(internal, 7, 104, 13)));

        // 8 — выходной слот
        this.customSlots.put(44, this.addSlot(new SlotItemHandler(internal, 8, 139, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    Utils.addAchievement(serverPlayer, "concoction:oven_cooking");
                }
            }
        }));
    }

    // ===== Состояние для GUI (берём из BlockEntity, как раньше) =====

    public int getProgress() {
        if (boundBlockEntity instanceof OvenBlockEntity be) {
            return be.getProgress();
        }
        return progress;
    }

    public int getMaxProgress() {
        if (boundBlockEntity instanceof OvenBlockEntity be) {
            return be.getMaxProgress();
        }
        return maxProgress;
    }

    public boolean isCooking() {
        if (boundBlockEntity instanceof OvenBlockEntity be) {
            return be.isCooking();
        }
        return isCooking;
    }

    public boolean isLit() {
        if (boundBlockEntity instanceof OvenBlockEntity be) {
            return be.isLit();
        }
        return isLit;
    }

    // ===== Реализация абстрактных методов RecipeBookMenu =====

    /** Кладём ингредиенты в StackedContents, чтобы рецептбук понимал, что мы можем скрафтить. */
    @Override
    public void fillCraftSlotsStackedContents(StackedContents contents) {
        // наши крафтовые слоты — GUI 37..42 (внутренние индексы 1..6)
        for (int slotIndex = 37; slotIndex <= 42; slotIndex++) {
            if (slotIndex >= 0 && slotIndex < this.slots.size()) {
                Slot slot = this.slots.get(slotIndex);
                if (slot != null && slot.hasItem()) {
                    contents.accountSimpleStack(slot.getItem());
                }
            }
        }
    }

    /** Ванильные меню очищают крафтовые слоты, нам это не надо. */
    @Override
    public void clearCraftingContent() {
        // no-op, духовка сама управляет инвентарём через блок-энтити
    }

    /** Фильтруем, какие рецепты вообще подходят этому меню. */
    @Override
    public boolean recipeMatches(RecipeHolder<OvenRecipe> recipeHolder) {
        // это духовка, ей подходят только OvenRecipe
        return recipeHolder != null && recipeHolder.value() != null;
    }

    /** Индекс выходного слота в this.slots. */
    @Override
    public int getResultSlotIndex() {
        return 44; // наш output слот
    }

    /** Размер "виртуальной сетки" для рецептбука. */
    @Override
    public int getGridWidth() {
        return 3; // 3 по ширине
    }

    @Override
    public int getGridHeight() {
        return 2; // 2 по высоте
    }

    /** Общий размер инвентаря, который видит книга. */
    @Override
    public int getSize() {
        return this.slots.size();
    }

    /** Тип книги рецептов. ХАК: используем SMOKER, чтобы не пересекаться с печкой. */
    @Override
    public RecipeBookType getRecipeBookType() {
        // какой "стиль" рецептбука использовать — берём от печки
        return RecipeBookType.FURNACE;
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        // какие категории вообще есть у ЭТОГО меню
        // тут мы хотим только вкладку с едой печи
        return List.of(RecipeBookCategories.FURNACE_FOOD);
    }




    /** Из каких слотов книга будет переносить вещи в инвентарь игрока. */
    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex == getResultSlotIndex();
    }

    // ===== обычная логика меню (как в рабочей версии) =====

    @Override
    public boolean stillValid(Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return RecipeBookMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // из духовки в игрока
            if (index >= 36 && index <= 44) {
                if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            // из игрока в духовку
            else if (index >= 0 && index < 36) {
                boolean moved = false;

                // сначала пробуем засунуть посуду в слот миски (#c:tableware)
                if (itemstack1.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tableware")))) {
                    if (this.moveItemStackTo(itemstack1, 43, 44, false)) {
                        moved = true;
                    }
                }

                // потом — в бутылочку, потом — в ингредиенты
                if (!moved) {
                    if (this.moveItemStackTo(itemstack1, 36, 37, false)) {
                        moved = true;
                    } else if (this.moveItemStackTo(itemstack1, 37, 43, false)) {
                        moved = true;
                    }
                }

                if (!moved) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

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
        if (pos != null && world != null) {
            world.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    ConcoctionModSounds.OVEN_CLOSE.get(),
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0F,
                    false
            );
        }
    }
}
