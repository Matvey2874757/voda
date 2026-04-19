package com.example.vodateleport;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Educational client-side mod.
 * Intended for single-player testing or servers with disabled movement checks.
 */
public final class VodaTeleportClient implements ClientModInitializer {
    public static final String MOD_ID = "vodateleport";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static VodaTeleportClient instance;

    private ModConfig config;
    private KeyBinding openMenuKey;
    private final List<SavedPosition> savedPositions = new ArrayList<>();

    public static VodaTeleportClient getInstance() {
        return instance;
    }

    public List<SavedPosition> getSavedPositions() {
        return Collections.unmodifiableList(savedPositions);
    }

    public void addCurrentPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        int index = savedPositions.size() + 1;
        savedPositions.add(new SavedPosition(
                "Point " + index,
                client.player.getX(),
                client.player.getY(),
                client.player.getZ()
        ));
    }

    /**
     * Sends a movement packet with modified coordinates to simulate fast client teleport.
     */
    public void sendTeleportPacket(double x, double y, double z) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        // Instantly update local player to match expected destination.
        client.player.setPosition(x, y, z);

        // Educational packet simulation (works properly only where movement checks allow it).
        client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x,
                y,
                z,
                client.player.isOnGround(),
                false
        ));

        client.player.sendMessage(Text.literal("[VodaTeleport] Teleport packet sent: %.2f %.2f %.2f".formatted(x, y, z)), true);
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        config = ModConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vodateleport.open_menu",
                InputUtil.Type.KEYSYM,
                config.openGuiKey,
                "category.vodateleport"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                client.setScreen(new VodaTeleportScreen());
            }
        });

        LOGGER.info("VodaTeleport initialized. For local testing / servers without anti-cheat movement checks.");
    }
}
