package teal.panorama.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
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

    @Mutable
    @Final
    @Shadow
    private RotatingCubeMapRenderer backgroundRenderer;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    // Less laggy when loaded, also changes the panorama when called by other mods like Panorama Tweaker.
    @Inject(
        method = "initWidgetsNormal",
        at = @At("RETURN")
    )
    private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        Main.SKYBOX = this.backgroundRenderer;
        this.addButton(new ButtonWidget(4, 4, 60, 20, I18n.translate("panorama.title"), (b) ->
            MinecraftClient.getInstance().openScreen(new GuiPanoramaSelector())
        ));
    }
}
