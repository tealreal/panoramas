package teal.panorama.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.panorama.Main;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Inject(
        at = @At("TAIL"),
        method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"
    )
    private void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (Main.takePanorama && Main.index < 6) {
            Main.Facing f = Main.Facing.getIndex(Main.index);
            this.setRotation(f.yaw, f.pitch);
        }
    }

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
}
