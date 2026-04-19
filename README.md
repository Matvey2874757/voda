# Vanish Client (Fabric)

> Educational demonstration for Fabric client rendering + packet interception.

## What it does

- `H`: hides your own first-person hand/item and third-person (F5) local player model.
- `J`: enables movement-packet desync mode by suppressing outgoing `PlayerMoveC2SPacket` packets.

## Important protocol limitation

In vanilla Minecraft protocol, **other players seeing your entity is controlled by the server**. A pure client-side mod cannot force the server to stop tracking/sending your entity to other clients.

So this project gives:

1. Guaranteed local invisibility (your own camera/F5 view).
2. Best-effort network desync behavior that may make your server position stale or frozen from others' perspective on weakly protected servers.

It does **not** guarantee true remote invisibility in all servers.

## Build

```bash
./gradlew build
```
