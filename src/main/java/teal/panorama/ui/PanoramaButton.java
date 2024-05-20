package teal.panorama.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import teal.panorama.Main;
import teal.panorama.registry.PanoramaInstance;
import teal.panorama.util.Util;

public class PanoramaButton extends ButtonWidget {
    public static Identifier BORDER = new Identifier("panorama", "textures/gui/border.png");
    private final NativeImageBackedTexture icon;
    private final PanoramaInstance panorama;

    public PanoramaButton(PanoramaInstance panorama, int x, int y, TooltipSupplier tooltipSupplier) {
        super(x, y, 64, 64, Text.of(""), null, tooltipSupplier);
        this.panorama = panorama;
        this.icon = panorama.getIcon();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        icon.bindTexture();
        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, 0.0F, 0.0F, 64, 64, 64, 64);
        MinecraftClient.getInstance().getTextureManager().bindTexture(BORDER);
        drawTexture(matrices, this.x - 2, this.y - 2, 0.0F, 0.0F, 70, 70, 70, 70);
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
