package teal.panorama.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import teal.panorama.Main;

import java.util.Arrays;

@Mixin(CubeMapRenderer.class)
public abstract class CubeMapRendererMixin {

    @Shadow
    @Final
    private Identifier[] faces;

    @Redirect(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"
        )
    )
    private void setShaderTextureRedirect(int texture, Identifier id) {
        if (Main.textures != null && Main.textures.length >= 6) {
            RenderSystem.setShaderTexture(texture, Main.textures[Arrays.asList(faces).indexOf(id)].getGlId());
        } else RenderSystem.setShaderTexture(texture, id);
    }

}
