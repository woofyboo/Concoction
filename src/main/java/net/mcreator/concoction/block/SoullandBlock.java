
package net.mcreator.concoction.block;

import java.lang.reflect.Method;
import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource; 
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

import static net.mcreator.concoction.init.ConcoctionModBlocks.WEIGHTED_SOULS;
import oshi.driver.unix.solaris.disk.Prtvtoc;
@EventBusSubscriber(modid = ConcoctionMod.MODID)
public class SoullandBlock extends Block {
	public static final BooleanProperty SOULCHARGED = BooleanProperty.create("soulcharged");
//	static {
//		MOISTURE = BlockStateProperties.MOISTURE;
//		SHAPE = Block.box((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)15.0F, (double)16.0F);
//	}
	public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);
	
	public SoullandBlock() {
    super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .sound(SoundType.SOUL_SOIL)
            .strength(0.5f)
            .randomTicks()
            .isRedstoneConductor((bs, br, bp) -> false)
            .lightLevel(state -> state.getValue(SOULCHARGED) ? 3 : 0) // Используйте тернарный оператор для lightLevel
            .emissiveRendering((state, world, pos) -> state.getValue(SOULCHARGED)) // Эмиссивное освещение зависит от состояния SOULCHARGED
    );
    this.registerDefaultState(this.stateDefinition.any().setValue(SOULCHARGED, false));
}

	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		if (!event.getLevel().isClientSide) {
			if (event.getEntity() instanceof ServerPlayer player && player.getMainHandItem().is(ItemTags.HOES)) {
				BlockPos pos = event.getPos();
				Level world = event.getLevel();
				if (world.getBlockState(pos).is(Blocks.SOUL_SOIL)) {
					turnToSoil(player, world.getBlockState(pos), world, pos);
				}
			}
		}
	}

	@Override
	protected BlockState updateShape(BlockState p_53276_, Direction p_53277_, BlockState p_53278_, LevelAccessor p_53279_, BlockPos p_53280_, BlockPos p_53281_) {
		if (p_53277_ == Direction.UP && !p_53276_.canSurvive(p_53279_, p_53280_)) {
			p_53279_.scheduleTick(p_53280_, this, 1);
		}

		return super.updateShape(p_53276_, p_53277_, p_53278_, p_53279_, p_53280_, p_53281_);
	}

	@Override
	public boolean canSurvive(BlockState p_53272_, LevelReader p_53273_, BlockPos p_53274_) {
		BlockState blockstate = p_53273_.getBlockState(p_53274_.above());
		return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
	}



	@Override
	public void tick(BlockState p_221134_, ServerLevel p_221135_, BlockPos p_221136_, RandomSource p_221137_) {
		if (!p_221134_.canSurvive(p_221135_, p_221136_)) {
			turnToSoil((Entity)null, p_221134_, p_221135_, p_221136_);
		}
	}

	@Override
	public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
	    super.randomTick(blockstate, world, pos, random);
	    
	    boolean charged = blockstate.getValue(SOULCHARGED); // Check if the block is soul-charged
	    boolean nearSoul = isNearSoul(world, pos); // Check if there's a soul nearby
	
	    if (!nearSoul) {
	        // If no soul is near and the block is charged, reset the charge
	        if (charged) {
	            world.setBlock(pos, blockstate.setValue(SOULCHARGED, false), 2);
	        } else if (!shouldMaintainFarmland(world, pos)) {
	            // If not charged and no need to maintain farmland, turn it to soil
	            turnToSoil(null, blockstate, world, pos);
	        }
	    } else {
	        // If souls are nearby, charge the block
	        if (!charged) {
	            world.setBlock(pos, blockstate.setValue(SOULCHARGED, true), 2);
	        }
	
	        // If the block is soul-charged, speed up crop growth (5x faster)
	        if (charged) {
	            // Iterate over crops nearby and trigger growth multiple times (5x faster)
	            for (BlockPos offset : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
	                BlockState nearbyState = world.getBlockState(offset);
	                if (nearbyState.getBlock() instanceof CropBlock cropBlock) {
	                    try {
	                        // Use reflection to access the protected randomTick method
	                        Method randomTickMethod = CropBlock.class.getDeclaredMethod("randomTick", BlockState.class, ServerLevel.class, BlockPos.class, RandomSource.class);
	                        randomTickMethod.setAccessible(true);
	                        // Invoke the randomTick method using reflection
	                        randomTickMethod.invoke(cropBlock, nearbyState, world, offset, random);
	                    } catch (Exception e) {
	                        e.printStackTrace(); // Handle any exceptions that may occur
	                    }
	                }
	            }
	        }
	    }
	}
	



	@Override
	public void fallOn(Level p_153227_, BlockState p_153228_, BlockPos p_153229_, Entity p_153230_, float p_153231_) {
		if (!p_153227_.isClientSide && CommonHooks.onFarmlandTrample(p_153227_, p_153229_, Blocks.SOUL_SOIL.defaultBlockState(), p_153231_, p_153230_)) {
			turnToSoil(p_153230_, p_153228_, p_153227_, p_153229_);
		}

		super.fallOn(p_153227_, p_153228_, p_153229_, p_153230_, p_153231_);
	}

	public static void turnToSoil(@Nullable Entity p_270981_, BlockState p_270402_, Level p_270568_, BlockPos p_270551_) {
		BlockState blockstate = pushEntitiesUp(p_270402_, Blocks.SOUL_SOIL.defaultBlockState(), p_270568_, p_270551_);
		p_270568_.setBlockAndUpdate(p_270551_, blockstate);
		p_270568_.gameEvent(GameEvent.BLOCK_CHANGE, p_270551_, GameEvent.Context.of(p_270981_, blockstate));
	}

	private static boolean shouldMaintainFarmland(BlockGetter p_279219_, BlockPos p_279209_) {
		return p_279219_.getBlockState(p_279209_.above()).is(BlockTags.MAINTAINS_FARMLAND);
	}

	private static boolean isNearSoul(LevelReader level, BlockPos pPos) {
		BlockState state = level.getBlockState(pPos);

		for(BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-4, 0, -4), pPos.offset(4, 1, 4))) {
			if (level.getBlockState(blockpos).getBlock().equals(WEIGHTED_SOULS.get())) {
				return true;
			}
		}

		return FarmlandWaterManager.hasBlockWaterTicket(level, pPos);
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SOULCHARGED);
	}

	@Override
	public boolean isPathfindable(BlockState p_53267_, PathComputationType p_53270_) {
		return false;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(SOULCHARGED, false);
	}
}
