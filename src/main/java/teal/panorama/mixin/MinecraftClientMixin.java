package teal.panorama.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.panorama.Main;
import teal.panorama.registry.PanoramaRegistry;
import teal.panorama.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Unique
    private boolean checked;

    @Inject(
        at = @At("HEAD"),
        method = "render(Z)V"
    )
    private void render(boolean tick, CallbackInfo info) {
        if (!this.checked) {
            PanoramaRegistry.setup();
            try {
                File f = new File("panorama.dat");
                if (f.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String pack = reader.readLine();
                    Util.loadPack(pack);
                    reader.close();
                }
            } catch (Exception e) {
                Main.logger.error(e.getLocalizedMessage());
            }

            this.checked = true;
        }
    }

}
