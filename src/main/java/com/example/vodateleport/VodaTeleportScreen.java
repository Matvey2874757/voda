package com.example.vodateleport;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Manual GUI implementation using vanilla widgets/drawables.
 */
public final class VodaTeleportScreen extends Screen {
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget zField;

    protected VodaTeleportScreen() {
        super(Text.literal("Voda Teleport Menu"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = 40;

        xField = new TextFieldWidget(textRenderer, centerX - 110, y, 220, 20, Text.literal("X"));
        xField.setPlaceholder(Text.literal("X"));
        addDrawableChild(xField);

        y += 24;
        yField = new TextFieldWidget(textRenderer, centerX - 110, y, 220, 20, Text.literal("Y"));
        yField.setPlaceholder(Text.literal("Y"));
        addDrawableChild(yField);

        y += 24;
        zField = new TextFieldWidget(textRenderer, centerX - 110, y, 220, 20, Text.literal("Z"));
        zField.setPlaceholder(Text.literal("Z"));
        addDrawableChild(zField);

        y += 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("Телепорт по координатам"), btn -> doCoordinateTeleport())
                .dimensions(centerX - 110, y, 220, 20)
                .build());

        y += 24;
        addDrawableChild(ButtonWidget.builder(Text.literal("Сохранить текущую позицию"), btn -> {
                    VodaTeleportClient.getInstance().addCurrentPosition();
                    MinecraftClient.getInstance().setScreen(new VodaTeleportScreen());
                })
                .dimensions(centerX - 110, y, 220, 20)
                .build());

        addSavedPointsButtons();

        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), btn -> close())
                .dimensions(centerX - 50, height - 28, 100, 20)
                .build());
    }

    private void addSavedPointsButtons() {
        int centerX = width / 2;
        int y = 190;

        List<SavedPosition> points = VodaTeleportClient.getInstance().getSavedPositions();
        int maxToShow = Math.min(points.size(), 8);
        for (int i = 0; i < maxToShow; i++) {
            SavedPosition point = points.get(i);
            addDrawableChild(ButtonWidget.builder(Text.literal(point.toString()), btn ->
                            VodaTeleportClient.getInstance().sendTeleportPacket(point.x(), point.y(), point.z()))
                    .dimensions(centerX - 140, y, 280, 20)
                    .build());
            y += 24;
        }
    }

    private void doCoordinateTeleport() {
        try {
            double x = Double.parseDouble(xField.getText().trim());
            double y = Double.parseDouble(yField.getText().trim());
            double z = Double.parseDouble(zField.getText().trim());
            VodaTeleportClient.getInstance().sendTeleportPacket(x, y, z);
        } catch (NumberFormatException ex) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("[VodaTeleport] Invalid coordinates"), false);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer,
                "Используйте только в одиночной игре или на серверах без античита.",
                width / 2 - 150,
                170,
                0xFFAA55);
        super.render(context, mouseX, mouseY, delta);
    }
}
