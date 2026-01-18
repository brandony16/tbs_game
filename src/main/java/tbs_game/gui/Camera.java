package tbs_game.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Camera {

    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 4.0;

    private final double boardWidth;

    private final DoubleProperty centerX = new SimpleDoubleProperty(0);
    private final DoubleProperty centerY = new SimpleDoubleProperty(0);
    private final DoubleProperty zoom = new SimpleDoubleProperty(1.0);

    private final DoubleProperty offsetX = new SimpleDoubleProperty(0);
    private final DoubleProperty offsetY = new SimpleDoubleProperty(0);

    private double screenW;
    private double screenH;

    public Camera(double boardWidth) {
        this.boardWidth = boardWidth;
    }

    public void setScreenSize(double w, double h) {
        this.screenW = w;
        this.screenH = h;
        updateOffsets();

        centerX.addListener((obs, oldT, newT) -> updateOffsets());
        centerY.addListener((obs, oldT, newT) -> updateOffsets());
        zoom.addListener((obs, oldT, newT) -> updateOffsets());
    }

    private void updateOffsets() {
        offsetX.set(centerX.get() - screenW / (2 * zoom.get()));
        offsetY.set(centerY.get() - screenH / (2 * zoom.get()));
    }

    public DoubleProperty centerXProperty() {
        return centerX;
    }

    public DoubleProperty centerYProperty() {
        return centerY;
    }

    public double getCenterX() {
        return centerX.get();
    }

    public double getCenterY() {
        return centerY.get();
    }

    public DoubleProperty zoomProperty() {
        return zoom;
    }

    public double getOffsetX() {
        return offsetX.get();
    }

    public double getOffsetY() {
        return offsetY.get();
    }

    public double getZoom() {
        return zoom.get();
    }

    public void zoom(double zoomFactor) {
        zoom.set(Math.clamp(zoom.get() * zoomFactor, 0.5, 4));
    }

    public void resetZoom() {
        zoom.set(1.0);
    }

    public void pan(double dx, double dy) {
        centerX.set(centerX.get() - dx / zoom.get());
        centerY.set(centerY.get() - dy / zoom.get());

        // // Wrap camera back around to give infinite wrapping effect
        double halfWidth = boardWidth / 2.0;
        if (centerX.get() > halfWidth) {
            centerX.set(centerX.get() - boardWidth);
        } else if (centerX.get() < -halfWidth) {
            centerX.set(centerX.get() + boardWidth);
        }
    }

    public void zoomAt(double zoomFactor, double mouseX, double mouseY) {
        double oldZoom = zoom.get();
        double newZoom = Math.clamp(oldZoom * zoomFactor, MIN_ZOOM, MAX_ZOOM);

        if (newZoom == oldZoom) {
            return;
        }

        // Screen-space offset from center
        double dx = mouseX - screenW / 2.0;
        double dy = mouseY - screenH / 2.0;


        // Adjust center so the world point under the mouse stays fixed
        double newCenterX = centerX.get() + dx / oldZoom - dx / newZoom;
        double newCenterY = centerY.get() + dy / oldZoom - dy / newZoom;

        centerX.set(newCenterX);
        centerY.set(newCenterY);

        zoom.set(newZoom);
    }

    public void snapTo(double wx, double wy) {
        centerX.set(wx);
        centerY.set(wy);
    }

    public void snapToPixelGrid() {
        centerX.set(Math.round(centerX.get()));
        centerY.set(Math.round(centerY.get()));
    }
}
