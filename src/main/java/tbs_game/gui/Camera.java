package tbs_game.gui;

import javafx.geometry.Point2D;

public class Camera {

    private double offsetX;
    private double offsetY;
    private double zoom = 1.0;

    public double getX() {
      return this.offsetX;
    }

    public double getY() {
      return this.offsetY;
    }

    public Point2D worldToScreen(double wx, double wy) {
        return new Point2D(
                (wx - offsetX) * zoom,
                (wy - offsetY) * zoom
        );
    }

    public Point2D screenToWorld(double sx, double sy) {
        return new Point2D(
                sx / zoom + offsetX,
                sy / zoom + offsetY
        );
    }

    public void pan(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
    }

    public void setCenter(double wx, double wy, double screenW, double screenH) {
        offsetX = wx - screenW / (2 * zoom);
        offsetY = wy - screenH / (2 * zoom);
    }
}
