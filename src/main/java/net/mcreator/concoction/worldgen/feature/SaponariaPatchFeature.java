package net.mcreator.concoction.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.*;

public class SaponariaPatchFeature extends Feature<SaponariaPatchConfig> {

    public SaponariaPatchFeature(Codec<SaponariaPatchConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SaponariaPatchConfig> ctx) {
        LevelAccessor level = ctx.level();
        RandomSource rand = ctx.random();
        BlockPos origin = ctx.origin();
        SaponariaPatchConfig cfg = ctx.config();

        // достаём блок из реестра по ResourceLocation
        var blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
        Block saponaria = blockRegistry.getOptional(cfg.block()).orElseThrow();


        // находим поверхность земли
        BlockPos start = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, origin);
        start = findFirstSolidBelow(level, start);
        if (start == null) return false;

        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> placedCenters = new ArrayList<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        int radius = Math.max(1, cfg.radius());
        int tries = Math.max(radius * 2, cfg.spreadTries());

        while (!queue.isEmpty() && tries-- > 0) {
            BlockPos cur = queue.poll();
            boolean placedAny = tryAttachAround(level, rand, cur, saponaria, cfg.attemptsPerPos());
            if (placedAny) placedCenters.add(cur);

            for (Direction d : Direction.values()) {
                BlockPos np = cur.relative(d);
                if (!visited.add(np)) continue;
                if (manhattan(np, start) > radius) continue;
                queue.add(np);
            }
        }

        if (placedCenters.isEmpty()) return false;

        final BlockPos center = start; // <--- добавили копию для лямбды
        int maxD = placedCenters.stream().mapToInt(p -> manhattan(p, center)).max().orElse(1);

        BlockPos min = start.offset(-radius, -1, -radius);
        BlockPos max = start.offset(radius, 1, radius);


        IntegerProperty ageProp = findIntProp(saponaria.defaultBlockState(), "age");
        IntegerProperty vigorProp = findIntProp(saponaria.defaultBlockState(), "vigor");

        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            BlockState st = level.getBlockState(p);
            if (!st.is(saponaria)) continue;

            int d = Math.min(maxD, manhattan(p, start));
            double t = maxD == 0 ? 0.0 : (d / (double) maxD); // 0..1
            int val = clamp(Math.round((float) ((1.0 - t) * 2.0)), 0, 2);

            if (ageProp != null && st.hasProperty(ageProp)) st = st.setValue(ageProp, val);
            if (vigorProp != null && st.hasProperty(vigorProp)) st = st.setValue(vigorProp, val);

            level.setBlock(p, st, Block.UPDATE_CLIENTS);
        }

        return true;
    }

    // поиск первой твёрдой поверхности под заданной позицией
    private static BlockPos findFirstSolidBelow(LevelReader level, BlockPos pos) {
        BlockPos.MutableBlockPos m = pos.mutable();
        for (int i = 0; i < 16; i++) {
            BlockState below = level.getBlockState(m.below());
            if (below.isAir()) {
                m.move(0, -1, 0);
                continue;
            }
            return m.immutable();
        }
        return null;
    }

    private boolean tryAttachAround(LevelAccessor level, RandomSource rand, BlockPos center,
                                    Block saponaria, int attempts) {
        boolean placed = false;
        for (int i = 0; i < attempts; i++) {
            BlockPos p = center.offset(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);
            if (canReplace(level, p)) {
                BlockState state = makeAttachedState(level, p, saponaria);
                if (state != null) {
                    level.setBlock(p, state, Block.UPDATE_CLIENTS);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private static boolean canReplace(LevelAccessor level, BlockPos pos) {
        BlockState st = level.getBlockState(pos);
        return st.isAir(); // теперь спавнится только в воздухе
    }


    private BlockState makeAttachedState(LevelAccessor level, BlockPos pos, Block saponaria) {
        BlockState base = saponaria.defaultBlockState();
        StateDefinition<Block, BlockState> def = base.getBlock().getStateDefinition();

        Map<Direction, BooleanProperty> dirProps = new EnumMap<>(Direction.class);
        dirProps.put(Direction.UP, findBoolProp(def, "up"));
        dirProps.put(Direction.DOWN, findBoolProp(def, "down"));
        dirProps.put(Direction.NORTH, findBoolProp(def, "north"));
        dirProps.put(Direction.SOUTH, findBoolProp(def, "south"));
        dirProps.put(Direction.EAST, findBoolProp(def, "east"));
        dirProps.put(Direction.WEST, findBoolProp(def, "west"));

        boolean any = false;
        BlockState st = base;

        for (Direction d : Direction.values()) {
            BlockPos neighbor = pos.relative(d);
            BlockState nb = level.getBlockState(neighbor);
            if (nb.isFaceSturdy(level, neighbor, d.getOpposite())) {
                BooleanProperty prop = dirProps.get(d);
                if (prop != null && st.hasProperty(prop)) {
                    st = st.setValue(prop, true);
                    any = true;
                }
            }
        }
        return any ? st : null;
    }

    private static IntegerProperty findIntProp(BlockState sample, String name) {
        for (var p : sample.getProperties()) {
            if (p instanceof IntegerProperty ip && p.getName().equalsIgnoreCase(name)
                    && ip.getPossibleValues().containsAll(List.of(0, 1, 2))) return ip;
        }
        return null;
    }

    private static BooleanProperty findBoolProp(StateDefinition<Block, BlockState> def, String name) {
        for (var p : def.getProperties()) {
            if (p instanceof BooleanProperty bp && p.getName().equalsIgnoreCase(name)) return bp;
        }
        return null;
    }

    private static int manhattan(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) +
                Math.abs(a.getY() - b.getY()) +
                Math.abs(a.getZ() - b.getZ());
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
