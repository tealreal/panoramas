package teal.panorama.registry;

import net.minecraft.client.texture.NativeImageBackedTexture;
import teal.panorama.Config;
import teal.panorama.Main;
import teal.panorama.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanoramaRegistry {
    public static List<PanoramaInstance> PANORAMAS = new ArrayList<>();

    public static void setup() {
        PANORAMAS.clear();
        File file = new File(Config.INSTANCE.save_directory);
        if (!file.exists()) {
            file.mkdirs();
        }

        for (File f : file.listFiles()) {
            if (f.getName().endsWith(".zip")) {
                try {
                    Util.loadPackIcon(f.getName());
                } catch (Exception e) {
                    Main.logger.error(e.getLocalizedMessage());
                }
            }
        }

    }

    public static void addPanorama(String s, NativeImageBackedTexture icon) {
        PanoramaInstance p = new PanoramaInstance(s);
        p.setIcon(icon);
        PANORAMAS.add(p);
    }

    public static List<PanoramaInstance> getAllForName(String s) {
        List<PanoramaInstance> l = new ArrayList<>();

        for (PanoramaInstance panorama : PANORAMAS) {
            if (panorama.getPanoramaName().toLowerCase().contains(s.toLowerCase())) {
                l.add(panorama);
            }
        }

        return l;
    }
}
