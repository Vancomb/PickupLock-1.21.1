# Changelog

All notable changes to Pickup Lock are listed here. This project follows
[Semantic Versioning](https://semver.org/).

## [1.0.1] - 2026-10-24

### Fixed

- Lock button no longer appears hidden on the creative inventory's **Survival Inventory** tab. It was
  drawn under that tab's slots, items and 3D player model; it now draws on top and shows on every tab.

## [1.0.0] - 2026-10-24

### Added

- Initial release.
- Lock toggle that stops you picking up items off the ground while active.
- Lock button in the survival and creative inventories; Shift-drag to reposition (saved per screen).
- Toggle keybind (unbound by default), usable anywhere.
- Lock state saved per player; survives relog, death and dimension changes.
- Optional networking: the button hides itself on servers without the mod, so vanilla play still works.
