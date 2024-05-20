package teal.panorama.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import teal.panorama.Main;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getHorizontalPlane()Lnet/minecraft/util/math/Vec3f;"
        )
    )
    private static Vec3f constSkyColor(Camera camera) {
        return Main.takePanorama ? new Vec3f(0.0F, 1.0F, 0.0F) : camera.getHorizontalPlane();
    }
}
