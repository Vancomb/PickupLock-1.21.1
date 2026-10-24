package net.vancomb.pickuplock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.lwjgl.glfw.GLFW;

/**
 * The lock toggle shown in the inventory. A plain left-click toggles the lock; Shift-drag moves the
 * button, and the new spot is saved to {@link ClientConfig}. The 16x16 source icons are scaled down
 * to {@link #SIZE} when drawn. The survival and creative inventories keep independent positions, so
 * the button sits in its own spot in each; in creative it shows on every tab.
 */
public class LockButton extends AbstractButton {
    private static final int SIZE = 12;
    private static final int TEXTURE_SIZE = 16;

    private static final ResourceLocation LOCKED_ICON =
            ResourceLocation.fromNamespaceAndPath(PickupLock.MODID, "textures/gui/lock.png");
    private static final ResourceLocation UNLOCKED_ICON =
            ResourceLocation.fromNamespaceAndPath(PickupLock.MODID, "textures/gui/lock_unlocked.png");

    private boolean dragging;
    /** Mouse position relative to the button's top-left at the moment the drag started. */
    private int grabX;
    private int grabY;

    public LockButton() {
        super(0, 0, SIZE, SIZE, Component.translatable("pickuplock.button.narration"));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            if (dragging) {
                // Follow the cursor, clamped on-screen, until the mouse button comes back up.
                int newX = Mth.clamp(mouseX - grabX, 0, screen.width - SIZE);
                int newY = Mth.clamp(mouseY - grabY, 0, screen.height - SIZE);
                setX(newX);
                setY(newY);
                if (!isLeftMouseDown()) {
                    dragging = false;
                    configX().set(getX() - screen.getGuiLeft());
                    configY().set(getY() - screen.getGuiTop());
                }
            } else {
                // Re-anchor from the live GUI origin each frame so the button follows the inventory
                // when the recipe book opens and shifts it sideways.
                setX(screen.getGuiLeft() + configX().get());
                setY(screen.getGuiTop() + configY().get());
            }
        }

        ResourceLocation icon = PickupLock.clientLocked ? LOCKED_ICON : UNLOCKED_ICON;
        graphics.blit(icon, getX(), getY(), SIZE, SIZE, 0.0F, 0.0F, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Shift + left-click starts a drag instead of toggling. mouseClicked reaches the button
        // before the container screen's slot handling, so this never triggers a quick-move.
        if (this.active && this.visible && button == 0 && clicked(mouseX, mouseY) && Screen.hasShiftDown()) {
            dragging = true;
            grabX = (int) mouseX - getX();
            grabY = (int) mouseY - getY();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onPress() {
        PickupLockClient.toggleLock();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    /** The X offset for the current screen: the creative panel has its own, kept apart from survival. */
    private static ModConfigSpec.IntValue configX() {
        return Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen
                ? ClientConfig.CREATIVE_BUTTON_X
                : ClientConfig.BUTTON_X;
    }

    /** The Y offset for the current screen; see {@link #configX()}. */
    private static ModConfigSpec.IntValue configY() {
        return Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen
                ? ClientConfig.CREATIVE_BUTTON_Y
                : ClientConfig.BUTTON_Y;
    }

    private static boolean isLeftMouseDown() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        return GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }
}
