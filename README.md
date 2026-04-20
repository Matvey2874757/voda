# Teleport Utility Mod (Fabric)

Client-side Fabric mod for Minecraft **1.21.1** that opens a teleport utility GUI with:

- Online player list (live-updated) and teleport buttons.
- Coordinate inputs (X/Y/Z) + teleport button.
- Save current position + list of saved points.

## Important note
This implementation uses regular `/tp` commands sent from client chat.
It works only where you have permission to use teleport commands.

## Build

```bash
gradle build
```

(or `./gradlew build` if wrapper is added)
