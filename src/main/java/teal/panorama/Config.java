package teal.panorama;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Config {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    public static Config INSTANCE;
    public Main.CaptureResolution save_resolution;
    public String save_directory;

    public Config() {
        this.save_resolution = Main.CaptureResolution.DEFAULT;
        this.save_directory = "mods/panorama/";
    }

    public static void load() {
        File file = new File("config/panorama.json");
        if (!file.exists()) {
            generate();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String s;

            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }

            reader.close();
            INSTANCE = GSON.fromJson(sb.toString(), Config.class);
            if (INSTANCE.save_resolution == null) {
                INSTANCE.save_resolution = Main.CaptureResolution.DEFAULT;
            }
        } catch (Exception var4) {
            INSTANCE = new Config();
        }

        File f = new File(INSTANCE.save_directory);
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    private static void generate() {
        File file = new File("config/panorama.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        INSTANCE = new Config();

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(GSON.toJson(INSTANCE));
            writer.close();
        } catch (Exception ignored) {
        }

    }

    public static void save() {
        File file = new File("config/panorama.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(GSON.toJson(INSTANCE));
            writer.close();
        } catch (Exception ignored) {
        }

    }
}
