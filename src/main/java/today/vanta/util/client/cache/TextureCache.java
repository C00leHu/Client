package today.vanta.util.client.cache;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    private static final Map<String, Integer> textureMap = new HashMap<>();

    public static int getTexture(String key, BufferedImage image) {
        if (textureMap.containsKey(key)) {
            return textureMap.get(key);
        }

        int texId = uploadTexture(image);
        textureMap.put(key, texId);
        return texId;
    }

    private static int uploadTexture(BufferedImage image) {
        int textureId = GL11.glGenTextures();
        GlStateManager.bindTexture(textureId);

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
                buffer.put((byte) (pixel & 0xFF));         // B
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }
        buffer.flip();

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        return textureId;
    }

    public static void deleteAll() {
        for (int texId : textureMap.values()) {
            GL11.glDeleteTextures(texId);
        }
        textureMap.clear();
    }
}