
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.world.inventory.OvenMenu;
import net.mcreator.concoction.world.inventory.KitchenCabinetMenu;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ConcoctionMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<KitchenCabinetMenu>> KITCHEN_CABINET_INTERFACE = REGISTRY.register("kitchen_cabinet_interface", () -> IMenuTypeExtension.create(KitchenCabinetMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<OvenMenu>> OVEN_GUI = REGISTRY.register("oven_gui", () -> IMenuTypeExtension.create(OvenMenu::new));
}
