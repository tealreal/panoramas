package teal.panorama.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.panorama.Config;
import teal.panorama.Main;
import teal.panorama.util.Util;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "render",
        at = @At("RETURN")
    )
    private void render(CallbackInfo ci) {
        if (Main.takePanorama) {
            client.options.perspective = 0;
            client.options.hudHidden = true;
            if (Main.timer >= 1) {
                Main.timer = 0;
                if (Main.index < 6) {
                    Main.takeScreenshot(Main.index);
                    ++Main.index;
                } else {
                    if (Config.INSTANCE.save_resolution != Main.CaptureResolution.DEFAULT) {
                        client.getWindow().onFramebufferSizeChanged(MinecraftClient.getInstance().getWindow().handle, Main.WIN_BACK_WIDTH, Main.WIN_BACK_HEIGHT);
                    }

                    Main.IMAGES.put(Main.currentName, Main.screenshots);
                    Main.takePanorama = false;
                    ((RTCAccessor) ((MCAccessor) client).getRenderTickCounter()).setTickTime((float) 1000 / 20);
                    client.options.perspective = Main.PERSPECTIVE;
                    client.options.hudHidden = false;

                    try {
                        Util.zipFiles(Main.currentName);
                    } catch (Exception e) {
                        Main.logger.error(e.getLocalizedMessage());
                    }
                }
            } else {
                ++Main.timer;
            }
        } else {
            Main.timer = 0;
        }
    }
}
