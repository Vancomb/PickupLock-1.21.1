package net.vancomb.pickuplock.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.vancomb.pickuplock.PickupLock;

/**
 * Client -> server. Sent when the player flips the lock, either with the inventory button or the
 * keybind. Carries the new state the server should store on the player.
 */
public record SetLockPayload(boolean locked) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetLockPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PickupLock.MODID, "set_lock"));

    public static final StreamCodec<ByteBuf, SetLockPayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(SetLockPayload::new, SetLockPayload::locked);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
