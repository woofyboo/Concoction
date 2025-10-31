package net.mcreator.concoction.enchantments;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.mcreator.concoction.ConcoctionMod;

public record ButcheringEnchantmentEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<ButcheringEnchantmentEffect> CODEC = MapCodec.unit(ButcheringEnchantmentEffect::new);

    @Override
    public void apply(ServerLevel serverLevel, int enchantLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
    }
//jopa
    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
