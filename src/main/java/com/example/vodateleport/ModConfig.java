package com.example.vodateleport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple JSON config.
 * You can change the GUI key by editing config/vodateleport.json.
 */
public final class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("vodateleport.json");

    public int openGuiKey = GLFW.GLFW_KEY_P;

    public static ModConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            ModConfig config = new ModConfig();
            config.save();
            return config;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ModConfig fromFile = GSON.fromJson(reader, ModConfig.class);
            if (fromFile == null) {
                ModConfig config = new ModConfig();
                config.save();
                return config;
            }
            return fromFile;
        } catch (IOException | JsonSyntaxException ex) {
            VodaTeleportClient.LOGGER.warn("Failed to load config, using defaults", ex);
            return new ModConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ex) {
            VodaTeleportClient.LOGGER.error("Failed to save config", ex);
        }
    }
}
