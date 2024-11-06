package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class SunflowerRightClickProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != event.getEntity().getUsedItemHand())
			return;
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == ConcoctionModBlocks.SUNFLOWER.get()
				&& ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("age") instanceof IntegerProperty _getip3 ? (world.getBlockState(BlockPos.containing(x, y, z))).getValue(_getip3) : -1) == 5
				&& ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("half") instanceof EnumProperty _getep5 ? (world.getBlockState(BlockPos.containing(x, y, z))).getValue(_getep5).toString() : "")
						.equals("upper")) {
			{
				int _value = 4;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == ConcoctionModBlocks.SUNFLOWER.get()) {
				{
					int _value = 4;
					BlockPos _pos = BlockPos.containing(x, y - 1, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("age") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
						world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
				}
			}
			if (!world.isClientSide()) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.cave_vines.pick_berries")), SoundSource.PLAYERS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.cave_vines.pick_berries")), SoundSource.PLAYERS, 1, 1, false);
					}
				}
			}
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, (x + 0.5), (y + 0.5), (z + 0.5), new ItemStack(Items.WHEAT_SEEDS));
				entityToSpawn.setPickUpDelay(10);
				_level.addFreshEntity(entityToSpawn);
			}
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, (x + 0.5), (y + 0.5), (z + 0.5), new ItemStack(Items.WHEAT_SEEDS));
				entityToSpawn.setPickUpDelay(10);
				_level.addFreshEntity(entityToSpawn);
			}
			if (Math.random() < 0.5) {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, (x + 0.5), (y + 0.5), (z + 0.5), new ItemStack(Items.WHEAT_SEEDS));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
			}
			if (entity instanceof LivingEntity _entity)
				_entity.swing(InteractionHand.MAIN_HAND, true);
		}
	}
}
