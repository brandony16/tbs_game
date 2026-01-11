package tbs_game.gui;

public class Camera {

    private double originX;
    private double originY;

    private double offsetX = 0;
    private double offsetY = 0;
    private double zoom = 1.0;

    public void setOrigin(double ox, double oy) {
        this.originX = ox;
        this.originY = oy;
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
    }

    public void setCenter(double wx, double wy, double screenW, double screenH) {
        offsetX = wx - screenW / (2 * zoom);
        offsetY = wy - screenH / (2 * zoom);
    }
}
