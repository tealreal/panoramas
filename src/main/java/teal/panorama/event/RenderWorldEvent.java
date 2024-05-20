package teal.panorama.event;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.End;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import teal.panorama.Config;
import teal.panorama.Main;
import teal.panorama.mixin.MCAccessor;
import teal.panorama.mixin.RTCAccessor;
import teal.panorama.util.Util;

public class RenderWorldEvent implements End {
    public void onEnd(WorldRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (Main.takePanorama) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
            mc.options.hudHidden = true;
            if (Main.timer >= 1) {
                Main.timer = 0;
                if (Main.index < 6) {
                    Main.takeScreenshot(Main.index);
                    ++Main.index;
                } else {
                    if (Config.INSTANCE.save_resolution != Main.CaptureResolution.DEFAULT) {
                        mc.getWindow().onFramebufferSizeChanged(MinecraftClient.getInstance().getWindow().handle, Main.WIN_BACK_WIDTH, Main.WIN_BACK_HEIGHT);
                    }

                    Main.IMAGES.put(Main.currentName, Main.screenshots);
                    Main.takePanorama = false;
                    ((RTCAccessor) ((MCAccessor) mc).getRenderTickCounter()).setTickTime((float) 1000 / 20);
                    mc.options.setPerspective(Main.PERSPECTIVE);
                    mc.options.hudHidden = false;

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
