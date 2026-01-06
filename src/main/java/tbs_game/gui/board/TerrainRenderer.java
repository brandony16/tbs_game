package tbs_game.gui.board;

import java.util.Objects;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tbs_game.board.Terrain;
import tbs_game.game.Game;
import tbs_game.gui.AssetManager;

public final class TerrainRenderer {

    private static final Random RNG = new Random(Game.SEED);

    public static Node renderBaseTerrain(Terrain terrain, double cx, double cy) {
        Image img = AssetManager.getImage(getBaseTerrainPath(terrain));
        ImageView iv = new ImageView(img);

        iv.setFitWidth(BoardView.TILE_RADIUS * 2);
        iv.setFitHeight(BoardView.TILE_RADIUS * 2);
        iv.setX(cx - BoardView.TILE_RADIUS);
        iv.setY(cy - BoardView.TILE_RADIUS);

        return iv;
    }

    public static Node renderOverlayTerrain(Terrain terrain, double cx, double cy) {
        if (terrain == Terrain.FOREST) {
            return renderForest(cx, cy);
        }

        if (terrain == Terrain.MOUNTAIN) {
            return renderMountain(cx, cy);
        }

        return null;
    }

    private static String getBaseTerrainPath(Terrain terrain) {
        switch (terrain) {
            case PLAINS, WATER -> {
                return terrain.spritePath;
            }
            case FOREST -> {
                return "/terrain/plains.png";
            }
            case MOUNTAIN -> {
                return "/terrain/mountainBase.png";
            }
            default ->
                throw new AssertionError();
        }
    }

    private static Node renderForest(double cx, double cy) {
        Group group = new Group();
        Random rand = new Random(Objects.hash(cx, cy));

        for (int i = 0; i < 5; i++) {
            ImageView tree = new ImageView(
                    AssetManager.getImage("/terrain/tree.png")
            );

            double scale = 0.8 + rand.nextDouble() * 0.4;
            tree.setFitWidth(BoardView.TILE_RADIUS * scale);
            tree.setFitHeight(BoardView.TILE_RADIUS * scale);

            double x = cx - BoardView.TILE_RADIUS + rand.nextDouble() * BoardView.TILE_RADIUS * 2;
            double y = cy - BoardView.TILE_RADIUS + rand.nextDouble() * BoardView.TILE_RADIUS * 2;

            tree.setX(x);
            tree.setY(y);

            group.getChildren().add(tree);
        }

        return group;
    }

    private static Node renderMountain(double cx, double cy) {
        Group group = new Group();
        Random rand = new Random(Objects.hash(cx, cy) * 31);

        ImageView mountain = new ImageView(
                AssetManager.getImage("/terrain/mountain.png")
        );
        double scale = 1.0 + rand.nextDouble() * 0.1;
        double size = BoardView.TILE_RADIUS * 2 * scale;
        mountain.setFitWidth(size);
        mountain.setFitHeight(size);

        double x = cx - size / 2;
        double y = cy - size / 2 - 10;

        mountain.setX(x);
        mountain.setY(y);
        group.getChildren().add(mountain);
        return group;
    }
}
