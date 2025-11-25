package net.mcreator.concoction.client;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(
        modid = ConcoctionMod.MODID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ConcoctionClientColors {

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Тинт для блоков-листьев
        event.register((state, level, pos, tintIndex) -> {
            // tintIndex == 0 — те самые "покрашиваемые" части модели
            if (tintIndex == 0) {
                if (level != null && pos != null) {
                    // как ванильные листья: цвет листвы биома
                    return BiomeColors.getAverageFoliageColor(level, pos);
                }
                // fallback, когда уровня/позиции нет (например, в инвентаре)
                return FoliageColor.getDefaultColor();
            }
            // остальные слои не красим
            return 0xFFFFFFFF;
        }, new Block[]{
                ConcoctionModBlocks.CINNAMON_LEAVES.get()
        });
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Тинт для айтема листьев — просто берём тот же цвет, что и у блока
        event.register((stack, tintIndex) -> {
            Block block = ConcoctionModBlocks.CINNAMON_LEAVES.get();
            return Minecraft.getInstance()
                    .getBlockColors()
                    .getColor(block.defaultBlockState(), null, null, tintIndex);
        }, ConcoctionModBlocks.CINNAMON_LEAVES.get().asItem());
    }
}
