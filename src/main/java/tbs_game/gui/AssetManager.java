package tbs_game.gui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public final class AssetManager {

    private static final Map<String, Image> imageCache = new HashMap<>();

    private AssetManager() {
    }

    public static Image getImage(String path) {
        return imageCache.computeIfAbsent(path, AssetManager::loadImage);
    }

    private static Image loadImage(String path) {
        InputStream stream = AssetManager.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Missing asset: " + path);
        }

        return new Image(stream);
    }

    public static void clear() {
        imageCache.clear();
    }
}
