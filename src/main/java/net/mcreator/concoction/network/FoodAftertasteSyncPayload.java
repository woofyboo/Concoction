package net.mcreator.concoction.network;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public record FoodAftertasteSyncPayload(List<CompoundTag> aftertasteEntries) implements CustomPacketPayload {
    public static final Type<FoodAftertasteSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "food_aftertaste_sync")
    );
    public static final StreamCodec<FriendlyByteBuf, FoodAftertasteSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FoodAftertasteSyncPayload decode(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<CompoundTag> entries = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                CompoundTag tag = buffer.readNbt();
                entries.add(tag != null ? tag : new CompoundTag());
            }
            return new FoodAftertasteSyncPayload(entries);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, FoodAftertasteSyncPayload payload) {
            buffer.writeVarInt(payload.aftertasteEntries().size());
            for (CompoundTag entry : payload.aftertasteEntries()) {
                buffer.writeNbt(entry);
            }
        }
    };

    public static void register() {
        ConcoctionMod.addNetworkMessage(
                TYPE,
                STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        FoodAftertasteHandler.applyClientSync(context.player(), payload.aftertasteEntries())
                )
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
