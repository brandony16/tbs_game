package tbs_game.gui.board;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tbs_game.board.Terrain;
import tbs_game.gui.AssetManager;

public final class TerrainRenderer {

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
                return "/terrain/plains.png";
            }
            default ->
                throw new AssertionError();
        }
    }

    private static Node renderForest(double cx, double cy) {
        Group group = new Group();
        Random rand = new Random(Objects.hash(cx, cy));

        int targetTrees = 6;

        double hexRadius = BoardView.TILE_RADIUS;
        double maxRadius = hexRadius * 0.65;   // stay mostly inside hex
        double minDist = hexRadius * 0.5;     // spacing between trees
        double verticalBias = hexRadius * 0.25;

        List<Point2D> placed = new ArrayList<>();

        int attempts = 0;
        int maxAttempts = 40;

        while (placed.size() < targetTrees && attempts < maxAttempts) {
            attempts++;

            double r = Math.sqrt(rand.nextDouble()) * maxRadius;
            double angle = rand.nextDouble() * Math.PI * 2;

            double x = cx + r * Math.cos(angle);
            double y = cy + r * Math.sin(angle);

            y += verticalBias;

            boolean tooClose = false;
            for (Point2D p : placed) {
                if (p.distance(x, y) < minDist) {
                    tooClose = true;
                    break;
                }
            }

            if (tooClose) {
                continue;
            }

            placed.add(new Point2D(x, y));
        }

        placed.sort(Comparator.comparingDouble(Point2D::getY));
        for (Point2D point : placed) {
            Image img = AssetManager.getImage("/terrain/tree.png");
            ImageView tree = new ImageView(img);

            double scale = 0.85 + rand.nextDouble() * 0.3;
            double height = hexRadius * scale;
            tree.setFitHeight(height);
            tree.setPreserveRatio(true);
            tree.setSmooth(true);

            // Anchor tree to ground (bottom-center)
            double relativeScale = (height) / img.getHeight();
            double width = relativeScale * img.getWidth();

            tree.setX(point.getX() - width / 2);
            tree.setY(point.getY() - tree.getFitHeight());
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
        double y = cy - size / 2 - BoardView.TILE_RADIUS / 4;

        mountain.setX(x);
        mountain.setY(y);
        group.getChildren().add(mountain);
        return group;
    }
}
