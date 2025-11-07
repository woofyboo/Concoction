package net.mcreator.concoction.system;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class FoodTasteApplier {

    @SubscribeEvent
    public static void onFinishUse(LivingEntityUseItemEvent.Finish event) {
        ItemStack eaten = event.getItem(); // исходная еда
        if (eaten.isEmpty()) return;

        LivingEntity eater = event.getEntity();

        // В 1.21+: еда, если у стака есть FoodProperties для данного entity
        if (eaten.getFoodProperties(eater) == null) return;

        applyTaste(eater, eaten.get(FOOD_EFFECT.value()));
        applyTaste(eater, eaten.get(FOOD_EFFECT_2.value()));
        applyTaste(eater, eaten.get(FOOD_EFFECT_3.value()));
        applyTaste(eater, eaten.get(FOOD_EFFECT_4.value()));
        applyTaste(eater, eaten.get(FOOD_EFFECT_5.value()));
    }


    private static void applyTaste(LivingEntity entity, FoodEffectComponent comp) {
        if (comp == null) return;

        if (comp.type() == FoodEffectType.HEAL) {
            FoodEffectType.applyInstantEffect(comp.type(), entity, comp.level());
            return;
        }

        MobEffectInstance inst = FoodEffectType.getEffect(
                comp.type(), comp.level(), comp.duration(), comp.isHidden(), entity
        );
        if (inst != null) entity.addEffect(inst);
    }
}
