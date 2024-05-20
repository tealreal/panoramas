package teal.panorama.event;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BlockOutline;
import teal.panorama.Main;

public class RenderEvent implements BlockOutline {
    public boolean onBlockOutline(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlineContext) {
        return !Main.takePanorama;
    }
}
