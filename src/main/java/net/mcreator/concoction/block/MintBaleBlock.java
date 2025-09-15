package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;

public class MintBaleBlock extends Block {
	public static final net.minecraft.world.level.block.state.properties.DirectionProperty FACING = DirectionalBlock.FACING;

	// Порог высоты падения (в блоках), при котором накладывается эффект
	private static final float MIN_FALL_DISTANCE = 1.0F;

	// Настройки эффекта
	private static final int MINTY_BREATH_DURATION_TICKS = 200; // 10 сек
	private static final int MINTY_BREATH_AMPLIFIER = 0;        // уровень 0
	private static final boolean MINTY_BREATH_AMBIENT = false;
	private static final boolean MINTY_BREATH_SHOW_PARTICLES = false; // скрыть частицы
	private static final boolean MINTY_BREATH_SHOW_ICON = true;      // скрыть иконку

	public MintBaleBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.GRASS).strength(0.5f));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public void fallOn(net.minecraft.world.level.Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		// Пониженный урон от падения
		entity.causeFallDamage(fallDistance, 0.2F, world.damageSources().fall());

		// Накладываем эффект только при падении минимум на 1 блок и только на сервере
		if (!world.isClientSide && fallDistance >= MIN_FALL_DISTANCE && entity instanceof LivingEntity living) {
			ResourceLocation id = ConcoctionModMobEffects.MINTY_BREATH.getId();
			if (id != null) {
				ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);
				Holder<MobEffect> mintyHolder = BuiltInRegistries.MOB_EFFECT.getHolder(key).orElse(null);

				if (mintyHolder != null) {
					// 6-аргументный конструктор: (effectHolder, duration, amplifier, ambient, showParticles, showIcon)
					MobEffectInstance effect = new MobEffectInstance(
							mintyHolder,
							MINTY_BREATH_DURATION_TICKS,
							MINTY_BREATH_AMPLIFIER,
							MINTY_BREATH_AMBIENT,
							MINTY_BREATH_SHOW_PARTICLES,
							MINTY_BREATH_SHOW_ICON
					);
					living.addEffect(effect);
				}
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}
}
