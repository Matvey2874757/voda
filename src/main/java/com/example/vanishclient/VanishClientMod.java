package com.example.vanishclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class VanishClientMod implements ClientModInitializer {
    private static KeyBinding hideVisualKey;
    private static KeyBinding desyncModeKey;

    @Override
    public void onInitializeClient() {
        hideVisualKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vanishclient.toggle_visual",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.vanishclient"
        ));

        desyncModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vanishclient.toggle_desync",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.vanishclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        while (hideVisualKey.wasPressed()) {
            boolean next = !VanishState.isLocalVisualHide();
            VanishState.setLocalVisualHide(next);
            sendMessage(client, "[Vanish] Local render hide: " + (next ? "ON" : "OFF"));
        }

        while (desyncModeKey.wasPressed()) {
            boolean next = !VanishState.isPacketDesync();
            VanishState.setPacketDesync(next);
            sendMessage(client, "[Vanish] Movement packet desync mode: " + (next ? "ON" : "OFF"));
        }
    }

    private void sendMessage(MinecraftClient client, String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), true);
        }
    }
}
