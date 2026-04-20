package com.voda.teleportmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TeleportModClient implements ClientModInitializer {
    public static final String MOD_ID = "teleportmod";
    public static TeleportConfig CONFIG;

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        CONFIG = TeleportConfig.load();

        int keyCode = CONFIG.openGuiKey == 0 ? GLFW.GLFW_KEY_GRAVE_ACCENT : CONFIG.openGuiKey;
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.teleportmod.open_menu",
                InputUtil.Type.KEYSYM,
                keyCode,
                "category.teleportmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.player != null && client.world != null) {
                    client.setScreen(new TeleportScreen());
                }
            }
        });
    }
}
