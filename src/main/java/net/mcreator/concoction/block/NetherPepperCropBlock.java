package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;


import java.util.Objects;

// Класс растения, наследующий от CropBlock
public class NetherPepperCropBlock extends CropBlock {
	// Максимальный возраст растения
	public static final int MAX_AGE = 5;
	// Свойство возраста растения
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

	public NetherPepperCropBlock() {
		// Установка свойств блока
		super(BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT)
				.sound(SoundType.GRASS)
				.instabreak()
				.noCollission()
				.noOcclusion()
				.randomTicks()
				.pushReaction(PushReaction.DESTROY)
				
.lightLevel(s -> 3)
				.noOcclusion()
				.hasPostProcess((bs, br, bp) -> true)
				.emissiveRendering((bs, br, bp) -> true)
				.isRedstoneConductor((bs, br, bp) -> false));
		// Регистрация состояния по умолчанию
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
		}

		@Override
		public boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		    // Convert BlockGetter to LevelReader since canSurvive expects that
		    if (!(worldIn instanceof LevelReader)) return false;
		    return canSurvive(state, (LevelReader) worldIn, pos);
		}


	@Override
	protected boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
	    // Check if the block below can sustain this plant (usually farmland, dirt, etc)
	    BlockPos blockBelow = pos.below();
	    BlockState soil = worldIn.getBlockState(blockBelow);
	    return soil.getBlock() instanceof SoullandBlock;
	}

	protected void randomTick(BlockState p_221050_, ServerLevel p_221051_, BlockPos p_221052_, RandomSource p_221053_) {
		super.randomTick(p_221050_, p_221051_, p_221052_, p_221053_);
	}
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
	    super.onPlace(state, world, pos, oldState, isMoving);
	    if (!world.isClientSide()) {
	        ((ServerLevel) world).scheduleTick(pos, this, 1);
	    }
	}


	@Override
	protected ItemInteractionResult useItemOn(ItemStack pItem, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand p_316595_, BlockHitResult p_316140_) {
		if (!pPlayer.isShiftKeyDown() && pState.getBlock() == ConcoctionModBlocks.NETHER_PEPPER_CROP.get()) {
			if (pState.getValue(AGE) == 5) {
				pPlayer.swing(InteractionHand.MAIN_HAND, true);
				if (!pLevel.isClientSide())
					pLevel.playSound(null, pPos, Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.sweet_berry_bush.pick_berries"))), SoundSource.BLOCKS, 1, 1);
				else
					pLevel.playLocalSound(pPos, Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.sweet_berry_bush.pick_berries"))), SoundSource.BLOCKS, 1, 1, false);

				if (pLevel instanceof ServerLevel _level) {
					pLevel.setBlock(pPos, pState.setValue(AGE, 2), 3);
					ItemEntity entityToSpawn = new ItemEntity(_level, (pPos.getX() + 0.5), (pPos.getY() + 0.5), (pPos.getZ() + 0.5), new ItemStack(ConcoctionModItems.REAPPER.get(), 1));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);

					if (Math.random() < 0.3) {
					ItemEntity entityToSpawn3 = new ItemEntity(_level, (pPos.getX() + 0.5), (pPos.getY() + 0.5), (pPos.getZ() + 0.5), new ItemStack(ConcoctionModItems.REAPPER.get()));
						entityToSpawn3.setPickUpDelay(10);
						_level.addFreshEntity(entityToSpawn3);
					}
					return ItemInteractionResult.SUCCESS;
				}
			}
		}
		return super.useItemOn(pItem, pState, pLevel, pPos, pPlayer, p_316595_, p_316140_);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		// Пропускает ли блок свет вниз
		return true;
	}
	@Override
public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    long time = world.getDayTime() % 24000;
    if (time >= 17950 && time <= 18050) {
        for (Player player : world.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(pos).inflate(8))) {
            if (player.isAlive()) {
                // Damage player
                player.hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:soul_damage")))), 4);
                    double px = player.getX() + world.random.nextDouble() * 0.6 - 0.3;
					double py = player.getY() + player.getBbHeight() * 0.5 + world.random.nextDouble() * 0.6 - 0.3;
					double pz = player.getZ() + world.random.nextDouble() * 0.6 - 0.3;
					double vx = world.random.nextDouble() * 0.26 - 0.13;
					double vy = world.random.nextDouble() * 0.07 + 0.13;
					double vz = world.random.nextDouble() * 0.26 - 0.13;
					world.sendParticles(ParticleTypes.SOUL, px, py, pz, 1, vx, vy, vz, 0.2);

                
            }
        }
    }
    world.scheduleTick(pos, this, 20); // Schedule next tick in 1 second
}



	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		// Количество блокируемого света
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		// Визуальная форма блока
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		// Форма блока в зависимости от возраста
		return switch (state.getValue(AGE)) {
			default -> Block.box(1, 0, 1, 15, 15, 15);

			case 0 -> Block.box(4, 0, 4, 12, 8, 12);
			case 1 -> Block.box(2, 0, 2, 14, 12, 14);
			case 2 -> Block.box(1, 0, 1, 15, 15, 15);
			case 3 -> Block.box(1, 0, 1, 15, 15, 15);
			case 4 -> Block.box(1, 0, 1, 15, 15, 15);
			case 5 -> Block.box(1, 0, 1, 15, 15, 15);

		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		// Добавление свойства возраста в состояние блока
		builder.add(AGE);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		// Возвращает горючесть блока
		return 0;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		// Предмет, получаемый при копировании блока на колёсико
		return new ItemStack(
            ConcoctionModItems.REAPPER_SEEDS.get()
            );
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		// Скорость распространения огня
		return 0;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		// Тип пути для мобов
		return PathType.OPEN;
	}

	@Override
	public int getMaxAge() {
		// Возвращает максимальный возраст растения
		return MAX_AGE; // не менять
	}

	@Override
	protected ItemLike getBaseSeedId() {
		// Возвращает семена для посадки растения
		return ConcoctionModItems.REAPPER_SEEDS.get();
	}

	@Override
	public IntegerProperty getAgeProperty() {
		// Возвращает свойство возраста растения
		return AGE; // не менять
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(blockstate, world, pos, random);
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		// Normalize to day cycle
		long time = world.dayTime() % 24000;
		if (time >= 17950 && time <= 18050) {
			// Soul particles
			if (Math.random() < 0.65) {
				world.addParticle(
						ParticleTypes.SOUL,
						x + Mth.nextDouble(RandomSource.create(), 0.2, 0.8),
						y + Mth.nextDouble(RandomSource.create(), 0.2, 0.8),
						z + Mth.nextDouble(RandomSource.create(), 0.2, 0.8),
						Mth.nextDouble(RandomSource.create(), -0.13, 0.13),
						Mth.nextDouble(RandomSource.create(), 0.13, 0.2),
						Mth.nextDouble(RandomSource.create(), -0.13, 0.13)
				);
			}
			// Soul escape sound
			if (Math.random() < 0.1) {
				if (world instanceof Level _level) {
					ResourceLocation soulEscape = ResourceLocation.parse("particle.soul_escape");
					float pitch = (float)(0.8 + Math.random() * 0.4); // Random pitch between 0.8 and 1.2
					if (!_level.isClientSide()) {
						_level.playSound(
								null,
								BlockPos.containing(x, y, z),
								BuiltInRegistries.SOUND_EVENT.get(soulEscape),
								SoundSource.BLOCKS,
								0.6F, // 60% volume
								pitch
						);
					} else {
						_level.playLocalSound(
								x, y, z,
								BuiltInRegistries.SOUND_EVENT.get(soulEscape),
								SoundSource.BLOCKS,
								0.6F,
								pitch,
								false
						);
					}
				}
			}
			// Small chance for Ghast scream
			if (Math.random() < 0.02) {
				if (world instanceof Level _level) {
					float pitch = (float)(0.5 + ( Math.random() * 0.5 )); // Random pitch between 0.8 and 1.2
					if (!_level.isClientSide()) {
						_level.playSound(
								null,
								BlockPos.containing(x, y, z),
								SoundEvents.GHAST_HURT,
								SoundSource.HOSTILE,
								0.4F, // 60% volume
								pitch
						);
					} else {
						_level.playLocalSound(
								x, y, z,
								SoundEvents.GHAST_HURT,
								SoundSource.HOSTILE,
								0.4F,
								pitch,
								false
						);
					}
				}
			}
		}
	}
}
