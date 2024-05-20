package teal.panorama.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import teal.panorama.Main;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
        at = @At("HEAD"),
        method = "getFov",
        cancellable = true
    )
    private void getFovInject(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (Main.takePanorama) cir.setReturnValue(90.0);
    }

}
