package teal.panorama.util;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class TextureUtil {
    public static NativeImageBackedTexture loadInputstream(InputStream stream) throws Exception {
        NativeImage im = read(stream);
        return new NativeImageBackedTexture(im);
    }

    public static NativeImage read(InputStream inputStreamIn) throws IOException {
        return read(Format.RGBA, inputStreamIn);
    }

    public static NativeImage read(@Nullable Format pixelFormatIn, InputStream inputStreamIn) throws IOException {
        ByteBuffer bytebuffer = null;

        NativeImage nativeimage;
        try {
            bytebuffer = readToBuffer(inputStreamIn);
            bytebuffer.rewind();
            nativeimage = NativeImage.read(pixelFormatIn, bytebuffer);
        } finally {
            MemoryUtil.memFree(bytebuffer);
        }

        return nativeimage;
    }

    public static ByteBuffer readToBuffer(InputStream inputStreamIn) throws IOException {
        ByteBuffer bytebuffer;
        if (inputStreamIn instanceof FileInputStream) {
            FileChannel filechannel = ((FileInputStream) inputStreamIn).getChannel();
            bytebuffer = MemoryUtil.memAlloc((int) filechannel.size() + 1);

            while (true) {
                filechannel.read(bytebuffer);
            }
        } else {
            bytebuffer = MemoryUtil.memAlloc(8192);
            ReadableByteChannel readablebytechannel = Channels.newChannel(inputStreamIn);

            while (readablebytechannel.read(bytebuffer) != -1) {
                if (bytebuffer.remaining() == 0) {
                    bytebuffer = MemoryUtil.memRealloc(bytebuffer, bytebuffer.capacity() * 2);
                }
            }
        }

        return bytebuffer;
    }
}
