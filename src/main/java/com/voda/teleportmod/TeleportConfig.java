package com.voda.teleportmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JSON config for keybind + saved points.
 */
public final class TeleportConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type CONFIG_TYPE = new TypeToken<TeleportConfig>() {}.getType();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("teleportmod.json");

    public int openGuiKey = GLFW.GLFW_KEY_GRAVE_ACCENT;
    public List<SavedPoint> points = new ArrayList<>();

    public static TeleportConfig load() {
        if (Files.notExists(CONFIG_PATH)) {
            TeleportConfig cfg = new TeleportConfig();
            cfg.save();
            return cfg;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            TeleportConfig config = GSON.fromJson(reader, CONFIG_TYPE);
            return config == null ? new TeleportConfig() : config;
        } catch (IOException e) {
            return new TeleportConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, CONFIG_TYPE, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public String keyTranslationKey() {
        return InputUtil.Type.KEYSYM.createFromCode(openGuiKey).getTranslationKey();
    }

    public record SavedPoint(String name, double x, double y, double z) {}
}
