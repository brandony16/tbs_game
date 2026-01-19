package tbs_game.gui.camera;

import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import tbs_game.gui.camera.coord_systems.SceneDelta;
import tbs_game.gui.camera.coord_systems.ScenePos;

public class NewCamera {

    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 4.0;

    private final double boardWidthPx;

    private final Scale scale = new Scale(1, 1);
    private final Translate translate = new Translate(0, 0);

    public NewCamera(double boardWidthPx) {
        this.boardWidthPx = boardWidthPx;
    }

    public void attach(Node world) {
        world.getTransforms().addAll(translate, scale);
    }

    public double getOffsetX() {
        return this.translate.getX();
    }

    public double getOffsetY() {
        return this.translate.getY();
    }

    public double getZoom() {
        return this.scale.getX();
    }

    public void zoom(double factor) {
        scale.setX(clamp(scale.getX() * factor));
        scale.setY(clamp(scale.getY() * factor));
    }

    public void zoomAt(double zoomFactor, ScenePos pivot) {
        double oldZoom = scale.getX();
        double newZoom = clamp(oldZoom * zoomFactor);

        // Compute zoom ratio
        double zoomRatio = newZoom / oldZoom;

        // Adjust translation so that the pivot point stays fixed in scene coordinates
        translate.setX(translate.getX() - (pivot.x() - translate.getX()) * (zoomRatio - 1));
        translate.setY(translate.getY() - (pivot.y() - translate.getY()) * (zoomRatio - 1));

        // Apply new scale
        scale.setX(newZoom);
        scale.setY(newZoom);
    }

    public void panScene(SceneDelta delta) {
        translate.setX(translate.getX() - delta.dx());
        translate.setY(translate.getY() - delta.dy());
    }

    private double clamp(double z) {
        return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, z));
    }
}
