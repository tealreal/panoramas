package teal.panorama.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import teal.panorama.Main;
import teal.panorama.registry.PanoramaInstance;
import teal.panorama.util.Util;

public class PanoramaButton extends ButtonWidget {
    public static Identifier BORDER = new Identifier("panorama", "textures/gui/border.png");
    private final Identifier icon;
    private final PanoramaInstance panorama;

    public PanoramaButton(PanoramaInstance panorama, int x, int y) {
        super(x, y, 64, 64, Text.literal(""), null, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.panorama = panorama;
        this.setTooltip(Tooltip.of(Text.literal(panorama.getPanoramaName())));

        this.icon = new Identifier("panorama", panorama.getPanoramaName());
        MinecraftClient.getInstance().getTextureManager().registerTexture(icon, panorama.getIcon());
    }

    @Override
    public void setPosition(int xIn, int yIn) {
        this.setX(xIn);
        this.setY(yIn);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        RenderSystem.setShaderTexture(0, this.icon);
        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.getX(), this.getY(), 0.0F, 0.0F, 64, 64, 64, 64);
        RenderSystem.setShaderTexture(0, BORDER);
        drawTexture(matrices, this.getX() - 2, this.getY() - 2, 0.0F, 0.0F, 70, 70, 70, 70);
    }

    @Override
    public void onPress() {
        try {
            Util.loadPack(this.panorama.getPanoramaName());
        } catch (Exception e) {
            Main.logger.error(e.getLocalizedMessage());
        }
    }

    public PanoramaInstance getPanorama() {
        return this.panorama;
    }
}
