package local.survivalplus;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StatePayload(boolean enabled) implements CustomPacketPayload {
    public static final Type<StatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SurvivalPlus.ID, "state"));
    public static final StreamCodec<ByteBuf, StatePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, StatePayload::enabled, StatePayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
