package teal.panorama.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import teal.panorama.Config;
import teal.panorama.Main;
import teal.panorama.registry.PanoramaRegistry;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Util {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static File getName() {
        String fileName = "panorama-" + DATE_FORMAT.format(new Date());
        File file;
        int e = 1;
        while ((file = new File(Config.INSTANCE.save_directory, fileName + (e == 1 ? "" : "_" + e) + ".zip")).exists()) {
            ++e;
        }
        return file;
    }

    public static void zipFiles(String images) {
        (new Thread(() -> {
            NativeImage[] imgs = Main.IMAGES.remove(images);
            File file = new File(images);
            if (imgs != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(Config.INSTANCE.save_directory + file.getName());
                    ZipOutputStream zos = new ZipOutputStream(fos);

                    for (int i = 0; i < imgs.length; ++i) {
                        NativeImage image = imgs[i];
                        zos.putNextEntry(new ZipEntry("panorama_" + i + ".png"));
                        zos.write(image.getBytes());
                        zos.closeEntry();
                    }

                    zos.close();

                    MinecraftClient.getInstance().player.sendMessage(new TranslatableText("panorama.messages.saved").append(Text.of(images).copy().setStyle(Style.EMPTY.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())))), false);
                    PanoramaRegistry.addPanorama(file.getName(), new NativeImageBackedTexture(imgs[0]));
                } catch (IOException e) {
                    Main.logger.error("Error creating zip file: {}", e.getLocalizedMessage());
                }
            }
        })).start();
    }

    public static void loadPack(String packName) throws Exception {
        if (packName == null || packName.isEmpty()) {
            Main.textures = null;
        } else {
            NativeImageBackedTexture[] textures = new NativeImageBackedTexture[6];
            FileInputStream fis = new FileInputStream(Config.INSTANCE.save_directory + packName);

            try {
                BufferedInputStream bis = new BufferedInputStream(fis);

                try {
                    ZipInputStream zis = new ZipInputStream(bis);

                    try {
                        int index = 0;

                        while (true) {
                            ZipEntry ze;
                            if ((ze = zis.getNextEntry()) != null) {
                                if (!ze.getName().endsWith(".png")) {
                                    continue;
                                }

                                textures[index] = TextureUtil.loadInputstream(zis);
                                ++index;
                                if (index != 6) {
                                    continue;
                                }
                            }

                            zis.close();
                            bis.close();
                            zis.close();
                            break;
                        }
                    } catch (Throwable var10) {
                        try {
                            zis.close();
                        } catch (Throwable var9) {
                            var10.addSuppressed(var9);
                        }

                        throw var10;
                    }

                    zis.close();
                } catch (Throwable var11) {
                    try {
                        bis.close();
                    } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                    }

                    throw var11;
                }

                bis.close();
            } catch (Throwable var12) {
                try {
                    fis.close();
                } catch (Throwable var7) {
                    var12.addSuppressed(var7);
                }

                throw var12;
            }

            fis.close();
            boolean cont = true;
            byte var16 = 0;
            NativeImageBackedTexture t = textures[var16];
            if (t == null) {
                cont = false;
            }

            if (cont) {
                Main.textures = textures;
                FileWriter fw = new FileWriter("panorama.dat");
                fw.write(packName);
                fw.close();
            }

        }
    }

    public static void loadPackIcon(String packName) throws Exception {
        NativeImageBackedTexture texture = null;
        FileInputStream fis = new FileInputStream(Config.INSTANCE.save_directory + packName);

        try {
            BufferedInputStream bis = new BufferedInputStream(fis);

            try {
                ZipInputStream zis = new ZipInputStream(bis);

                try {
                    ZipEntry ze;
                    while ((ze = zis.getNextEntry()) != null) {
                        if (ze.getName().equals("icon.png")) {
                            texture = TextureUtil.loadInputstream(zis);
                            break;
                        }

                        if (ze.getName().equalsIgnoreCase("panorama_0.png")) {
                            texture = TextureUtil.loadInputstream(zis);
                        }
                    }

                    zis.close();
                    bis.close();
                } catch (Throwable var10) {
                    try {
                        zis.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }

                    throw var10;
                }

                zis.close();
            } catch (Throwable var11) {
                try {
                    bis.close();
                } catch (Throwable var8) {
                    var11.addSuppressed(var8);
                }

                throw var11;
            }

            bis.close();
        } catch (Throwable var12) {
            try {
                fis.close();
            } catch (Throwable var7) {
                var12.addSuppressed(var7);
            }

            throw var12;
        }

        fis.close();
        if (texture != null) {
            PanoramaRegistry.addPanorama(packName, texture);
        }

    }
}
