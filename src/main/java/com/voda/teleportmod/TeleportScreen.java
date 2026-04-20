package com.voda.teleportmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manual GUI implementation (without external GUI libs).
 * Teleport actions are sent as regular chat commands (/tp),
 * so they only work where the server allows them.
 */
public class TeleportScreen extends Screen {
    private final List<String> onlinePlayers = new ArrayList<>();
    private final List<ButtonWidget> dynamicButtons = new ArrayList<>();

    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget zField;

    private int playersOffset = 0;
    private int pointsOffset = 0;

    public TeleportScreen() {
        super(Text.literal("Teleport Utility"));
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 170;
        int top = this.height / 2 - 100;

        xField = new TextFieldWidget(textRenderer, left + 10, top + 20, 90, 20, Text.literal("X"));
        yField = new TextFieldWidget(textRenderer, left + 110, top + 20, 90, 20, Text.literal("Y"));
        zField = new TextFieldWidget(textRenderer, left + 210, top + 20, 90, 20, Text.literal("Z"));

        xField.setText("0");
        yField.setText("64");
        zField.setText("0");

        addDrawableChild(xField);
        addDrawableChild(yField);
        addDrawableChild(zField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Teleport by coords"), b -> tpByCoords())
                .dimensions(left + 10, top + 45, 160, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Save current position"), b -> saveCurrentPosition())
                .dimensions(left + 180, top + 45, 160, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Players ↑"), b -> {
                    playersOffset = Math.max(0, playersOffset - 1);
                    rebuildDynamicButtons();
                }).dimensions(left + 10, top + 75, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Players ↓"), b -> {
                    playersOffset++;
                    rebuildDynamicButtons();
                }).dimensions(left + 95, top + 75, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Points ↑"), b -> {
                    pointsOffset = Math.max(0, pointsOffset - 1);
                    rebuildDynamicButtons();
                }).dimensions(left + 180, top + 75, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Points ↓"), b -> {
                    pointsOffset++;
                    rebuildDynamicButtons();
                }).dimensions(left + 265, top + 75, 80, 20).build());

        refreshPlayers();
        rebuildDynamicButtons();
    }

    private void rebuildDynamicButtons() {
        for (ButtonWidget btn : dynamicButtons) {
            remove(btn);
        }
        dynamicButtons.clear();

        int left = this.width / 2 - 170;
        int top = this.height / 2 - 100;

        int playersVisible = Math.min(5, Math.max(0, onlinePlayers.size() - playersOffset));
        for (int i = 0; i < playersVisible; i++) {
            String name = onlinePlayers.get(playersOffset + i);
            int y = top + 105 + i * 20;
            ButtonWidget button = ButtonWidget.builder(Text.literal("TP to " + name), b -> tpToPlayer(name))
                    .dimensions(left + 10, y, 160, 20).build();
            dynamicButtons.add(addDrawableChild(button));
        }

        List<TeleportConfig.SavedPoint> points = TeleportModClient.CONFIG.points;
        if (pointsOffset >= points.size()) {
            pointsOffset = 0;
        }
        int pointsVisible = Math.min(5, Math.max(0, points.size() - pointsOffset));
        for (int i = 0; i < pointsVisible; i++) {
            TeleportConfig.SavedPoint point = points.get(pointsOffset + i);
            int y = top + 105 + i * 20;
            ButtonWidget button = ButtonWidget.builder(Text.literal(point.name()), b -> tpToPoint(point))
                    .dimensions(left + 180, y, 160, 20).build();
            dynamicButtons.add(addDrawableChild(button));
        }
    }

    private void refreshPlayers() {
        onlinePlayers.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            onlinePlayers.add(entry.getProfile().getName());
        }
        onlinePlayers.sort(Comparator.naturalOrder());
        if (playersOffset >= onlinePlayers.size()) {
            playersOffset = 0;
        }
    }

    private void tpByCoords() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        try {
            double x = Double.parseDouble(xField.getText());
            double y = Double.parseDouble(yField.getText());
            double z = Double.parseDouble(zField.getText());
            client.player.networkHandler.sendChatCommand("tp " + x + " " + y + " " + z);
        } catch (NumberFormatException ignored) {
        }
    }

    private void tpToPlayer(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        client.player.networkHandler.sendChatCommand("tp " + name);
    }

    private void tpToPoint(TeleportConfig.SavedPoint point) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        client.player.networkHandler.sendChatCommand("tp " + point.x() + " " + point.y() + " " + point.z());
    }

    private void saveCurrentPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        String name = "Point " + (TeleportModClient.CONFIG.points.size() + 1);
        TeleportModClient.CONFIG.points.add(new TeleportConfig.SavedPoint(
                name,
                client.player.getX(),
                client.player.getY(),
                client.player.getZ()
        ));
        TeleportModClient.CONFIG.save();
        rebuildDynamicButtons();
    }

    @Override
    public void tick() {
        super.tick();
        refreshPlayers();
        rebuildDynamicButtons();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int left = this.width / 2 - 170;
        int top = this.height / 2 - 100;
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, top - 12, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Online players"), left + 10, top + 92, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, Text.literal("Saved points"), left + 180, top + 92, 0xAAAAAA);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
