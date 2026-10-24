package net.vancomb.pickuplock;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.vancomb.pickuplock.network.SetLockPayload;
import net.vancomb.pickuplock.network.SyncLockPayload;

/**
 * Entry point and common (server + client) logic for Pickup Lock.
 *
 * <p>The mod is split so that nothing here touches client-only types: this class owns the saved
 * state, the networking and the actual pickup block, all of which must run on a dedicated server.
 * The inventory button and keybind live in {@link PickupLockClient}, which only loads on the client.
 */
@Mod(PickupLock.MODID)
public class PickupLock {
    public static final String MODID = "pickuplock";

    /**
     * The client's view of its own lock state. Lives in this common class (not a client-only one)
     * so the server->client sync handler can set it without pulling in client code. Read and
     * written on the client by the button and keybind; irrelevant on a dedicated server.
     */
    public static boolean clientLocked = false;

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    /**
     * Per-player lock state, stored on the server. Serialized so it survives relog and dimension
     * changes, and copied on death so it survives respawn.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> LOCKED =
            ATTACHMENTS.register("locked", () -> AttachmentType.builder(() -> Boolean.FALSE)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build());

    public PickupLock(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
        modEventBus.addListener(PickupLock::registerPayloads);

        NeoForge.EVENT_BUS.addListener(PickupLock::onItemPickup);
        NeoForge.EVENT_BUS.addListener(PickupLock::onPlayerLogin);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // optional(): clients and servers without the mod can still connect to ones that have it.
        event.registrar("1")
                .optional()
                .playToServer(SetLockPayload.TYPE, SetLockPayload.STREAM_CODEC, PickupLock::handleSetLock)
                .playToClient(SyncLockPayload.TYPE, SyncLockPayload.STREAM_CODEC, PickupLock::handleSyncLock);
    }

    /** Server: store the state the client just sent. Handlers run on the main thread. */
    private static void handleSetLock(SetLockPayload payload, IPayloadContext context) {
        context.player().setData(LOCKED.get(), payload.locked());
    }

    /** Client: adopt the saved state the server sent on login. */
    private static void handleSyncLock(SyncLockPayload payload, IPayloadContext context) {
        clientLocked = payload.locked();
    }

    /** The server decides pickups, so the block has to happen here. */
    private static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer().getData(LOCKED.get())) {
            event.setCanPickup(TriState.FALSE);
        }
    }

    /** Tell the joining client its saved state, but only if it actually has the mod. */
    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.connection.hasChannel(SyncLockPayload.TYPE)) {
            PacketDistributor.sendToPlayer(player, new SyncLockPayload(player.getData(LOCKED.get())));
        }
    }
}
