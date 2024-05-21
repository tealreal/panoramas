package teal.panorama.mixin;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.texture.TextureManager;
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
            target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V"
        )
    )
    private void setShaderTextureRedirect(TextureManager instance, Identifier id) {
        if (Main.textures != null && Main.textures.length >= 6) {
            Main.textures[Arrays.asList(faces).indexOf(id)].bindTexture();
        } else instance.bindTexture(id);
    }

}
