package tbs_game.gui.board;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class HexFactory {

    public static Polygon createHex(double cx, double cy) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30); // pointy-top
            double x = cx + BoardView.TILE_RADIUS * Math.cos(angle);
            double y = cy + BoardView.TILE_RADIUS * Math.sin(angle);
            hex.getPoints().addAll(x, y);
        }
        return hex;
    }

    public static Point2D[] hexCorners(double cx, double cy) {
        Point2D[] corners = new Point2D[6];
        for (int i = 0; i < 6; i++) {
            double angleRad = Math.toRadians(60 * i - 30);
            double x = cx + BoardView.TILE_RADIUS * Math.cos(angleRad);
            double y = cy - BoardView.TILE_RADIUS * Math.sin(angleRad); // +y in screen coords is down
            corners[i] = new Point2D(x, y);
        }
        return corners;
    }

}
