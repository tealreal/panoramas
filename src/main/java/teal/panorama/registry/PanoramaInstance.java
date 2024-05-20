package teal.panorama.registry;

import net.minecraft.client.texture.NativeImageBackedTexture;

public class PanoramaInstance {
    private final String panoramaName;
    private NativeImageBackedTexture icon;

    public PanoramaInstance(String s) {
        this.panoramaName = s;
    }

    public NativeImageBackedTexture getIcon() {
        return this.icon;
    }

    public void setIcon(NativeImageBackedTexture icon) {
        this.icon = icon;
    }

    public String getPanoramaName() {
        return this.panoramaName;
    }
}
