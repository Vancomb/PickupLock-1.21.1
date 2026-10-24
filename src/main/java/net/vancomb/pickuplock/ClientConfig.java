package net.vancomb.pickuplock;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Client-only config. Holds where the lock button sits, as an offset from the inventory's top-left
 * corner, so it follows the panel (and the recipe book) rather than being pinned to the window.
 * The survival and creative inventories keep separate offsets because their panels differ in size,
 * so a spot chosen in one never disturbs the other. Shift-dragging the button writes new values
 * here; NeoForge autosaves them to disk.
 */
public final class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BUTTON_X = BUILDER
            .comment("Lock button X offset from the survival inventory's top-left corner. Shift-drag the button to change it.")
            .defineInRange("buttonX", 156, -1024, 2048);

    public static final ModConfigSpec.IntValue BUTTON_Y = BUILDER
            .comment("Lock button Y offset from the survival inventory's top-left corner. Shift-drag the button to change it.")
            .defineInRange("buttonY", 3, -1024, 2048);

    public static final ModConfigSpec.IntValue CREATIVE_BUTTON_X = BUILDER
            .comment("Lock button X offset from the creative inventory's top-left corner. Shift-drag the button to change it.")
            .defineInRange("creativeButtonX", 156, -1024, 2048);

    public static final ModConfigSpec.IntValue CREATIVE_BUTTON_Y = BUILDER
            .comment("Lock button Y offset from the creative inventory's top-left corner. Shift-drag the button to change it.")
            .defineInRange("creativeButtonY", 3, -1024, 2048);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ClientConfig() {}
}
