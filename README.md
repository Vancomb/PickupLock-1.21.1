# Pickup Lock

A small NeoForge mod that adds a lock toggle to the player inventory. While locked, you stop
picking up items from the ground — useful for keeping your inventory tidy, protecting a curated
hotbar, or not hoovering up junk while mining or fighting.

- **Minecraft:** 1.21.1
- **Loader:** NeoForge 21.1.x (any 21.1 build)
- **Author:** Vancomb

## Features

- A lock button in the survival and creative inventories. Click it to toggle item pickup on or off.
- A toggle keybind (unbound by default — set it in **Options → Controls → Pickup Lock**). The keybind
  works anywhere, not just when the inventory is open.
- Shift-drag the button to move it; the position is saved and persists across worlds and restarts.
- Your lock state is saved per player and survives relog, death and dimension changes.
- Optional networking: works in singleplayer and multiplayer. On servers without the mod the button
  simply hides itself, so vanilla clients and servers can still connect normally.

## Usage

| Action | How |
| --- | --- |
| Toggle the lock | Left-click the inventory button, or press the keybind |
| Move the button | Hold **Shift** and drag it |
| Change the keybind | Options → Controls → Pickup Lock → "Toggle Pickup Lock" |

The button position is stored in `config/pickuplock-client.toml` if you'd rather set it by hand. The
survival and creative inventories keep separate offsets from their panel's top-left corner, so each
has its own spot: `buttonX` / `buttonY` for survival and `creativeButtonX` / `creativeButtonY` for
creative.

## Building

Requires JDK 21.

```
./gradlew build
```

The mod jar is written to `build/libs/`.

## License

Copyright © 2026 Vancomb. All rights reserved.

This mod is provided as a compiled download for personal use only. You may **not** redistribute,
reupload, repackage, port to other versions or loaders, or publish modified copies of this mod or
its source without explicit permission from the author.
