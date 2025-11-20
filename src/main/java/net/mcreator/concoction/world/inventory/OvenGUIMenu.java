package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.block.entity.OvenBlockEntity;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipeInput;
import net.mcreator.concoction.utils.Utils;
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
    // последний рецепт, который книга разложила в эту духовку
    private ResourceLocation lastRecipeId = null;
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

        if (!this.world.isClientSide && this.entity instanceof ServerPlayer serverPlayer) {
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

    /** Ванильные меню очищают крафтовые слоты, духовка сама управляет инвентарём через блок-энтити. */
    @Override
    public void clearCraftingContent() {
        // no-op
    }

    /** Фильтруем, какие рецепты вообще подходят этому меню. */
    @Override
    public boolean recipeMatches(RecipeHolder<OvenRecipe> recipeHolder) {
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

    /** Тип книги рецептов. */
    @Override
    public RecipeBookType getRecipeBookType() {
        // используем FURNACE, чтобы получить печную вкладку,
        // а содержимое фильтруем миксином
        return RecipeBookType.FURNACE;
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        // только еда печи
        return List.of(RecipeBookCategories.FURNACE_FOOD);
    }

    /** Из каких слотов книга будет переносить вещи в инвентарь игрока. */
    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex == getResultSlotIndex();
    }

    // ===== КАСТОМНАЯ АВТОСБОРКА РЕЦЕПТА =====

    /**
     * Автосборка по клику в книге рецептов.
     *
     * Делает так:
     * 1) Возвращает все предметы из слотов духовки (бутылка + 3×2 + миска) обратно игроку;
     * 2) Кладёт:
     *    - бутылку в слот 36;
     *    - миски в слот 43 (столько, сколько выдаёт рецепт);
     *    - крафтовые ингредиенты в слоты 37..42.
     *
     * Если чего-то не хватает — ничего не теряется, просто автосборка не полностью проходит.
     */
    @Override
    public void handlePlacement(boolean placeAll, RecipeHolder<?> holder, ServerPlayer player) {
        if (holder == null || holder.value() == null) {
            return;
        }

        if (!(holder.value() instanceof OvenRecipe ovenRecipe)) {
            return;
        }

        if (ovenRecipe.getType() != ConcoctionModRecipes.OVEN_RECIPE_TYPE.get()) {
            return;
        }

        // определяем, сменился ли рецепт
        ResourceLocation id = holder.id();
        boolean recipeChanged = (lastRecipeId == null || !lastRecipeId.equals(id));

        // если рецепт другой — возвращаем остатки прошлого крафта игроку
        if (recipeChanged) {
            returnOvenInputsToPlayer(player);
        }

        List<Ingredient> craftIngs = ovenRecipe.getCraftingIngredients();
        Ingredient bottleIng = ovenRecipe.getBottleIngredient();
        Ingredient bowlIng = ovenRecipe.getBowlIngredient();
        int bowlCost = ovenRecipe.getBowlCost(); // обычно равно количеству на выходе

        // сколько полных порций уже лежит в духовке
        int existingPatterns = getExistingPatterns(craftIngs, bowlIng, bowlCost);

        if (placeAll) {
            // SHIFT: гоним, пока есть ресурсы
            int patterns = existingPatterns;
            while (true) {
                int next = patterns + 1;
                if (!placePatternToReach(craftIngs, bottleIng, bowlIng, bowlCost, next)) {
                    break;
                }
                patterns = next;
            }
        } else {
            // обычный клик: хотим ещё +1 порцию поверх существующих
            int targetPatterns = existingPatterns + 1;
            placePatternToReach(craftIngs, bottleIng, bowlIng, bowlCost, targetPatterns);
        }

        lastRecipeId = id;
    }


    /**
     * Считает, сколько полных порций сейчас лежит в духовке.
     * Берём минимум из:
     *  - мисок (если они есть в рецепте),
     *  - количества в первом слоте сетки (если есть ингредиенты).
     */
    private int getExistingPatterns(List<Ingredient> craftIngs,
                                    Ingredient bowlIng,
                                    int bowlCost) {

        int patternsFromBowls = Integer.MAX_VALUE;
        if (!bowlIng.isEmpty() && bowlCost > 0 && this.slots.size() > 43) {
            Slot bowlSlot = this.slots.get(43);
            ItemStack bowlStack = bowlSlot.getItem();
            if (!bowlStack.isEmpty() && bowlIng.test(bowlStack)) {
                patternsFromBowls = bowlStack.getCount() / bowlCost;
            } else {
                patternsFromBowls = 0;
            }
        }

        int patternsFromGrid = Integer.MAX_VALUE;
        if (!craftIngs.isEmpty()) {
            // смотрим на первый слот сетки 3×2 (37)
            if (this.slots.size() > 37) {
                Slot firstGrid = this.slots.get(37);
                ItemStack gridStack = firstGrid.getItem();
                Ingredient firstIng = craftIngs.get(0);
                if (!gridStack.isEmpty() && firstIng.test(gridStack)) {
                    patternsFromGrid = gridStack.getCount();
                } else {
                    patternsFromGrid = 0;
                }
            } else {
                patternsFromGrid = 0;
            }
        }

        int patterns = Math.min(patternsFromBowls, patternsFromGrid);
        if (patterns == Integer.MAX_VALUE) {
            patterns = 0;
        }
        return patterns;
    }

    /**
     * Пытается довести содержимое слотов до состояния "targetPatterns порций".
     *
     * Например:
     *  - если targetPatterns = 2 и bowlCost = 2, то в слоте миски должно быть 4 штуки;
     *  - в каждом слоте сетки должно быть targetPatterns предметов.
     *
     * @return true, если удалось полностью собрать targetPatterns порций
     */
    private boolean placePatternToReach(List<Ingredient> craftIngs,
                                        Ingredient bottleIng,
                                        Ingredient bowlIng,
                                        int bowlCost,
                                        int targetPatterns) {

        // бутылка → по одной на порцию, если вообще нужна
        if (!bottleIng.isEmpty()) {
            int requiredBottles = targetPatterns; // 1 на каждую порцию
            if (!ensureCountInSlot(bottleIng, 36, requiredBottles)) {
                return false;
            }
        }


        // миски → targetPatterns * bowlCost
        if (!bowlIng.isEmpty() && bowlCost > 0) {
            int required = targetPatterns * bowlCost;
            if (!ensureCountInSlot(bowlIng, 43, required)) {
                return false;
            }
        }

        // крафтовые ингредиенты → targetPatterns штук в каждом слоте сетки
        int[] gridSlots = {37, 38, 39, 40, 41, 42};
        int max = Math.min(craftIngs.size(), gridSlots.length);

        for (int i = 0; i < max; i++) {
            Ingredient ing = craftIngs.get(i);
            if (ing.isEmpty()) continue;

            if (!ensureCountInSlot(ing, gridSlots[i], targetPatterns)) {
                return false;
            }
        }

        return true;
    }


    /**
     * Гарантирует наличие requiredCount предметов, подходящих под ingredient, в targetSlotIndex.
     *
     * - если слот пустой — тянем предметы из инвентаря игрока, пока не наберём requiredCount;
     * - если в слоте уже лежит подходящий предмет — просто докладываем до requiredCount;
     * - если там что-то другое — возвращаем false (мы заранее очищаем слоты, так что это маловероятно).
     *
     * НИКОГДА не тратит предметы, если не может удовлетворить запрос.
     */
    private boolean ensureCountInSlot(Ingredient ingredient, int targetSlotIndex, int requiredCount) {
        if (requiredCount <= 0) return true;
        if (targetSlotIndex < 0 || targetSlotIndex >= this.slots.size()) return false;

        Slot target = this.slots.get(targetSlotIndex);
        if (target == null) return false;

        ItemStack current = target.getItem();
        int have = 0;

        if (!current.isEmpty()) {
            if (!ingredient.test(current)) {
                // в слоте чужой предмет (но мы очищаем слоты до этого, так что маловероятно)
                return false;
            }
            have = current.getCount();
        }

        // добираем недостающее количество из инвентаря
        while (have < requiredCount) {
            ItemStack taken = takeOneMatchingFromPlayer(ingredient);
            if (taken.isEmpty()) {
                break; // не смогли взять ещё
            }

            if (current.isEmpty()) {
                current = taken;
            } else {
                current.grow(1);
            }
            have++;
        }

        if (current.isEmpty()) {
            target.set(ItemStack.EMPTY);
        } else {
            target.set(current);
        }
        target.setChanged();

        return have >= requiredCount;
    }

    /**
     * Ищет в инвентаре игрока (слоты 0..35) предмет, подходящий под ingredient,
     * забирает из него 1 штуку и возвращает отдельный стак размером 1.
     *
     * Если ничего не нашлось — возвращает ItemStack.EMPTY и НИЧЕГО не трогает.
     */
    private ItemStack takeOneMatchingFromPlayer(Ingredient ingredient) {
        for (int i = 0; i < 36 && i < this.slots.size(); i++) {
            Slot invSlot = this.slots.get(i);
            if (invSlot == null) continue;

            ItemStack stack = invSlot.getItem();
            if (!stack.isEmpty() && ingredient.test(stack)) {
                ItemStack result = stack.split(1); // отделяем 1 предмет
                if (stack.isEmpty()) {
                    invSlot.set(ItemStack.EMPTY);
                }
                invSlot.setChanged();
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Возвращает все предметы из слотов духовки (36..43) игроку:
     * сначала пытается положить в инвентарь, если не влезло — дропает под ноги.
     */
    private void returnOvenInputsToPlayer(ServerPlayer player) {
        int[] ovenSlots = {36, 37, 38, 39, 40, 41, 42, 43};
        for (int slotIndex : ovenSlots) {
            if (slotIndex < 0 || slotIndex >= this.slots.size()) continue;

            Slot slot = this.slots.get(slotIndex);
            if (slot == null) continue;

            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) continue;

            // пытаемся положить в инвентарь игрока (слоты 0..36)
            if (!this.moveItemStackTo(stack, 0, 36, false)) {
                // если не влезло — дропаем
                player.drop(stack, false);
            }

            slot.set(ItemStack.EMPTY);
            slot.setChanged();
        }
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
