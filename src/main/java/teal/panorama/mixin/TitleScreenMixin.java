package teal.panorama.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.panorama.Main;
import teal.panorama.ui.GuiPanoramaSelector;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(
        method = "initWidgetsNormal",
        at = @At("RETURN")
    )
    private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("panorama.title"), (b) ->
            MinecraftClient.getInstance().setScreen(new GuiPanoramaSelector())
        ).dimensions(4, 4, 60, 20).build());
    }
}
