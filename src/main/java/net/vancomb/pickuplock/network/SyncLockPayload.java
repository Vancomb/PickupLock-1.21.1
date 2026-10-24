package net.vancomb.pickuplock.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.vancomb.pickuplock.PickupLock;

/**
 * Server -> client. Sent on login so the client's copy of the lock state (which drives the button
 * icon and tooltip) matches what the server has saved for the player.
 */
public record SyncLockPayload(boolean locked) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncLockPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PickupLock.MODID, "sync_lock"));

    public static final StreamCodec<ByteBuf, SyncLockPayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(SyncLockPayload::new, SyncLockPayload::locked);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
