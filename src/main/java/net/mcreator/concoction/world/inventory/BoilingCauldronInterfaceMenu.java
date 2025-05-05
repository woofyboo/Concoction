package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class BoilingCauldronInterfaceMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	private Entity boundEntity = null;
	private BlockEntity boundBlockEntity = null;
	
	// Добавляем поля для хранения прогресса и состояния приготовления
	private int progress = 0;
	private int maxProgress = 200;
	private boolean isCooking = false;
	private boolean isLit = false;

	public BoilingCauldronInterfaceMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(ConcoctionModMenus.BOILING_CAULDRON_INTERFACE.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(6); // 4 слота для ингредиентов + 1 для половника + 1 для результата
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
		
		// Получаем доступ к блок-сущности котла
		if (pos != null && world.getBlockEntity(pos) instanceof CookingCauldronEntity cauldron) {
			this.boundBlockEntity = cauldron;
			// Создаем обертку InvWrapper для инвентаря котла
			this.internal = new InvWrapper(cauldron);
		}
		
		// Добавляем слоты для ингредиентов и инвентаря игрока
		addPlayerInventory(inv);
		addPlayerHotbar(inv);
		
		// Добавляем 4 слота для ингредиентов (2x2 сетка)
		this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 26, 27)));
		this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 44, 27)));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 26, 45)));
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 44, 45)));
		
		// Добавляем слот для половника/контейнера (5-й слот)
		this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 78, 15)));
		
		// Добавляем слот для результата (6-й слот)
		this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 118, 38) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; // нельзя положить предметы в результирующий слот
			}
			
			@Override
			public boolean mayPickup(Player player) {
				if (boundBlockEntity instanceof CookingCauldronEntity cauldron) {
					return true;
				}
				return false;
			}
			
			@Override
			public void onTake(Player player, ItemStack stack) {
				super.onTake(player, stack);
				// Проверяем, есть ли правильный контейнер в слоте половника
				ItemStack ladle = internal.getStackInSlot(4);
				if (!ladle.isEmpty()) {
					// Уменьшаем количество предметов в слоте половника
					internal.extractItem(4, 1, false);
					
					// Сбрасываем результат крафта в котле
					if (boundBlockEntity instanceof CookingCauldronEntity cauldron) {
						cauldron.setCraftResult(Map.of("id", "", "count", "", "interactionType", ""));

					}
					if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer)
						Utils.addAchievement(serverPlayer, "concoction:cauldron_boiling");
				}
			}
		}));
	}
	
	// Добавляем слоты инвентаря игрока
	private void addPlayerInventory(Inventory playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
			}
		}
	}
	
	// Добавляем хотбар игрока
	private void addPlayerHotbar(Inventory playerInventory) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}
	
	// Метод для получения текущего прогресса готовки
	public int getProgress() {
		if(boundBlockEntity instanceof CookingCauldronEntity entity) {
			return entity.getProgress();
		}
		return progress;
	}
	
	// Метод для получения максимального прогресса готовки
	public int getMaxProgress() {
		if(boundBlockEntity instanceof CookingCauldronEntity entity) {
			return entity.getMaxProgress();
		}
		return maxProgress;
	}
	
	// Метод для проверки, готовится ли что-то
	public boolean isCooking() {
		if(boundBlockEntity instanceof CookingCauldronEntity entity) {
			return entity.isCooking();
		}
		return isCooking;
	}
	
	// Метод для проверки, зажжен ли котел
	public boolean isLit() {
		if(boundBlockEntity instanceof CookingCauldronEntity entity) {
			return entity.isLit();
		}
		return isLit;
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound) {
			if (this.boundItemMatcher != null)
				return this.boundItemMatcher.get();
			else if (this.boundBlockEntity != null)
				return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
			else if (this.boundEntity != null)
				return this.boundEntity.isAlive();
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		if (slot != null && slot.hasItem()) {
			ItemStack stackInSlot = slot.getItem();
			itemstack = stackInSlot.copy();
			
			if (index == 5) {
				// Из слота результата в инвентарь
				if (!this.moveItemStackTo(stackInSlot, 6, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
				slot.onTake(playerIn, stackInSlot);
			} else if (index >= 6) {
				// Из инвентаря в слоты котла
				boolean moved = false;
				
				// Проверяем, является ли предмет палкой, миской, бутылкой или ведром
				if (stackInSlot.is(Items.STICK) || stackInSlot.is(Items.BOWL) || 
					stackInSlot.is(Items.GLASS_BOTTLE) || stackInSlot.is(Items.BUCKET)) {
					// Пытаемся переместить в слот половника (слот 4)
					if (this.slots.get(4).mayPlace(stackInSlot) && this.moveItemStackTo(stackInSlot, 4, 5, false)) {
						moved = true;
					}
				} 
				
				// Если предмет не переместился в слот половника или это обычный ингредиент
				if (!moved) {
					// Пытаемся переместить в слоты ингредиентов (0-3)
					if (!this.moveItemStackTo(stackInSlot, 0, 4, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (index < 5) {
				// Из слотов котла в инвентарь
				if (!this.moveItemStackTo(stackInSlot, 6, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			
			if (stackInSlot.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (stackInSlot.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(playerIn, stackInSlot);
			return itemstack;
		}
		return ItemStack.EMPTY;
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
