package tbs_game.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Camera {

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
    }

    private void updateOffsets() {
        offsetX.set(centerX.get() - screenW / (2 * zoom.get()));
        offsetY.set(centerY.get() - screenH / (2 * zoom.get()));
    }

    {
        centerX.addListener((obs, oldT, newT) -> updateOffsets());
        centerY.addListener((obs, oldT, newT) -> updateOffsets());
        zoom.addListener((obs, oldT, newT) -> updateOffsets());
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

    public void snapTo(double wx, double wy) {
        centerX.set(wx);
        centerY.set(wy);
    }

    public void snapToPixelGrid() {
        centerX.set(Math.round(centerX.get()));
        centerY.set(Math.round(centerY.get()));
    }
}
