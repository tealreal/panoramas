package teal.panorama;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teal.panorama.event.RenderEvent;
import teal.panorama.event.RenderWorldEvent;
import teal.panorama.mixin.MCAccessor;
import teal.panorama.mixin.RTCAccessor;
import teal.panorama.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main implements ClientModInitializer {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static NativeImageBackedTexture[] textures;
    public static int WIN_BACK_WIDTH;
    public static int WIN_BACK_HEIGHT;

    public static RotatingCubeMapRenderer SKYBOX;
    public static Map<String, NativeImage[]> IMAGES = new HashMap<>();
    public static Perspective PERSPECTIVE;
    public static String currentName = "";
    public static NativeImage[] screenshots;
    public static boolean takePanorama = false;
    public static int index = 0;
    public static int timer = 0;
    public static KeyBinding SCREENSHOT;

    public static void takeScreenshot(int index) {
        MinecraftClient client = MinecraftClient.getInstance();
        NativeImage image = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());
        File file = new File("temp/panorama_" + index + ".png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int fromY = 0;
        int dimension;
        int fromX;
        if (imageWidth > imageHeight) {
            dimension = imageHeight;
            fromX = imageWidth / 2 - imageHeight / 2;
        } else {
            dimension = imageWidth;
            fromX = 0;
        }

        NativeImage image2 = new NativeImage(dimension, dimension, true);
        image.resizeSubRectTo(fromX, fromY, dimension, dimension, image2);
        screenshots[index] = image2;
    }

    public void onInitializeClient() {
        Config.load();
        SCREENSHOT = KeyBindingHelper.registerKeyBinding(new KeyBinding("panorama.keybinds.screenshot", Type.KEYSYM, 293, "panorama.title"));
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().cameraEntity != null && SCREENSHOT.isPressed() && !takePanorama) {
                if (Config.INSTANCE.save_resolution != CaptureResolution.DEFAULT) {
                    WIN_BACK_WIDTH = MinecraftClient.getInstance().getWindow().getFramebufferWidth();
                    WIN_BACK_HEIGHT = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
                    MinecraftClient.getInstance().getWindow().onFramebufferSizeChanged(MinecraftClient.getInstance().getWindow().handle, Config.INSTANCE.save_resolution.res, Config.INSTANCE.save_resolution.res);
                }

                MinecraftClient mc = MinecraftClient.getInstance();
                RenderTickCounter rtc = ((MCAccessor) mc).getRenderTickCounter();
                ((RTCAccessor) rtc).setTickTime(Float.POSITIVE_INFINITY);

                PERSPECTIVE = mc.options.getPerspective();
                currentName = Util.getName().getName();
                screenshots = new NativeImage[6];
                takePanorama = true;
                index = 0;
            }

            if (takePanorama) {
                MinecraftClient.getInstance().options.hudHidden = true;
            }

        });
        WorldRenderEvents.BLOCK_OUTLINE.register(new RenderEvent());
        WorldRenderEvents.END.register(new RenderWorldEvent());
    }

    public enum CaptureResolution {
        DEFAULT(0),
        R_64(64),
        R_128(128),
        R_256(256),
        R_512(512),
        R_1024(1024),
        R_2048(2048),
        R_4096(4096),
        // THESE VALUES WILL LAG THE SHIT OUT OF YOUR COMPUTER WHEN TAKEN AND RENDERED.
        R_8192(8192),
        R_16384(16384);

        public static final CaptureResolution[] rs = CaptureResolution.values();
        public final MutableText name;
        public final int res;

        CaptureResolution(int i) {
            this.res = i;
            this.name = Text.of(i == 0 ? "Default" : (i + "px")).copy().append(
                Text.of((i > 4096) ? " !" : "").copy().formatted(Formatting.RED, Formatting.BOLD)
            );
        }

    }

    public enum Facing {
        SOUTH(0.0F, 0.0F),
        WEST(90.0F, 0.0F),
        NORTH(180.0F, 0.0F),
        EAST(-90.0F, 0.0F),
        UP(0.0F, -90.0F),
        DOWN(0.0F, 90.0F);

        public final float yaw;
        public final float pitch;

        Facing(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public static Facing getIndex(int index) {
            return values()[index];
        }

    }
}
