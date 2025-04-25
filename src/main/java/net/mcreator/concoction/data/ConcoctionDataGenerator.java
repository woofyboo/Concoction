package net.mcreator.concoction.data;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionDatapackProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionDataGenerator {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(
                event.includeServer(),
                new ConcoctionDatapackProvider(packOutput, event.getLookupProvider())
        );
        
        ConcoctionMod.LOGGER.info("Datapacks providers registered successfully!");
    }
} 