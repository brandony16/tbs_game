package tbs_game.gui;

public class Camera {

    private final double boardWidth;

    private double offsetX = 0;
    private double offsetY = 0;
    private double zoom = 1.0;

    public Camera(double boardWidth) {
        this.boardWidth = boardWidth;
    }

    public double getX() {
        return this.offsetX;
    }

    public double getY() {
        return this.offsetY;
    }

    public double getZoom() {
        return this.zoom;
    }

    public void zoom(double zoomFactor) {
        zoom *= zoomFactor;
        zoom = Math.clamp(zoom, 0.5, 4);
    }

    public void pan(double dx, double dy) {
        offsetX -= dx / zoom;
        offsetY -= dy / zoom;

        // Wrap camera back around to give infinite wrapping effect
        double halfWidth = boardWidth / 2.0;

        if (offsetX > halfWidth) {
            offsetX -= boardWidth;
        } else if (offsetX < -halfWidth) {
            offsetX += boardWidth;
        }
    }

    public void setCenter(double wx, double wy, double screenW, double screenH) {
        offsetX = wx - screenW / (2 * zoom);
        offsetY = wy - screenH / (2 * zoom);
    }
}
