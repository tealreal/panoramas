package teal.panorama.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.panorama.ui.GuiPanoramaSelector;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(
        at = @At("TAIL"),
        method = "init()V"
    )
    private void init(CallbackInfo info) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("panorama.title"), (b) ->
            MinecraftClient.getInstance().setScreen(new GuiPanoramaSelector())
        ).dimensions(4, 4, 60, 20).build());
    }
}
