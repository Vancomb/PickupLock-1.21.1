package net.vancomb.pickuplock;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.vancomb.pickuplock.network.SetLockPayload;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only setup: the keybind, the inventory button and the logout reset. This class is never
 * loaded on a dedicated server, so touching client types here is safe.
 */
@Mod(value = PickupLock.MODID, dist = Dist.CLIENT)
public class PickupLockClient {
    /** Toggle keybind. Unbound by default so it never clashes with vanilla or other mods. */
    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.pickuplock.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.pickuplock");

    public PickupLockClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        modEventBus.addListener(PickupLockClient::onRegisterKeyMappings);

        NeoForge.EVENT_BUS.addListener(PickupLockClient::onScreenInit);
        NeoForge.EVENT_BUS.addListener(PickupLockClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(PickupLockClient::onLoggingOut);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    /** Add the button to the survival and creative inventories, but only when the server has the mod. */
    private static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if ((screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) && serverHasMod()) {
            event.addListener(new LockButton());
        }
    }

    /** Drain any queued key presses; toggling from the keybind works anywhere, not just the inventory. */
    private static void onClientTick(ClientTickEvent.Post event) {
        boolean toggled = false;
        while (TOGGLE_KEY.consumeClick()) {
            toggled = true;
        }
        if (toggled) {
            toggleLock();
        }
    }

    /** Forget the state on disconnect; the next server we join sends its own on login. */
    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        PickupLock.clientLocked = false;
    }

    /**
     * Flip the lock and tell the server. Shared by the button and the keybind. No-op when the
     * server lacks the mod, so a local-only flip can't give a misleading icon.
     */
    static void toggleLock() {
        if (!serverHasMod()) {
            return;
        }
        PickupLock.clientLocked = !PickupLock.clientLocked;
        PacketDistributor.sendToServer(new SetLockPayload(PickupLock.clientLocked));
    }

    /** True when the server we're connected to also runs Pickup Lock. */
    static boolean serverHasMod() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(SetLockPayload.TYPE);
    }
}
