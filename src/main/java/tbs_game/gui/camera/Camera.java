package tbs_game.gui.camera;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import tbs_game.gui.coord_systems.SceneDelta;
import tbs_game.gui.coord_systems.ScenePos;
import tbs_game.gui.coord_systems.WorldPos;

public class Camera {
  private final CameraModel model;
  private final Scale scale = new Scale(1, 1);
  private final Translate translate = new Translate(0, 0);

  private Timeline snapTimeline;

  public Camera(double boardWidthPx) {
    this.model = new CameraModel(boardWidthPx);
  }

  /**
   * Sets the scene size of the camera. Should generally only be called once for
   * setup, after the root is attached to the scene.
   *
   * @param w - width in pixels
   * @param h - height in pixels
   */
  public void setSceneSize(double w, double h) {
    model.setSceneSize(w, h);
    syncTransforms();
  }

  /**
   * Updates the width of the scene. Adjusts translation to keep items centered
   *
   * @param newWidth - the new width of the scene
   */
  public void updateSceneWidth(double newWidth) {
    model.updateSceneWidth(newWidth);
    syncTransforms();
  }

  /**
   * Updates the height of the scene. Adjusts translation to keep items centered
   *
   * @param newHeight - the new height of the scene
   */
  public void updateSceneHeight(double newHeight) {
    model.updateSceneHeight(newHeight);
    syncTransforms();
  }

  /**
   * Center of the scene in scene coordinates. Not affected by camera
   * transforms.
   *
   * @return - ScenePos of the center coords
   */
  public ScenePos getSceneCenter() {
    return model.getSceneCenter();
  }

  /**
   * Attaches the camera transforms to the world node for auto updating.
   * Applies transformations in the order: translate -> scale
   *
   * @param world - the world node the camera will track
   */
  public void attach(Node world) {
    world.getTransforms().addAll(translate, scale);
  }

  /**
   * Gets the raw translation values in scene space. Corresponds to where (0, 0)
   * in world coordinates is.
   *
   * @return - Scene pos with of the offsets in the x and y directions
   */
  public ScenePos getSceneOffsets() {
    return new ScenePos(model.getX(), model.getY());
  }

  /**
   * Gets the zoom factor of the camera
   *
   * @return - zoom factor
   */
  public double getZoom() {
    return model.getZoom();
  }

  /**
   * Zooms by a factor around the origin.
   *
   * @param factor - factor to zoom by. < 1 for zoom out, > 1 for zoom in
   */
  public void zoom(double factor) {
    model.zoom(factor);
    syncTransforms();
  }

  /**
   * Zooms by a factor while keeping the pivot point fixed.
   *
   * @param zoomFactor - factor to zoom by. < 1 for zoom out, > 1 for zoom in
   * @param pivot      - the point to keep fixed
   */
  public void zoomAt(double zoomFactor, ScenePos pivot) {
    model.zoomAt(zoomFactor, pivot);
    syncTransforms();
  }

  /**
   * Pans the scene using a scene pixel delta.
   *
   * @param delta - the delta containing the increments to pan by
   */
  public void panScene(SceneDelta delta) {
    model.panScene(delta);
    syncTransforms();
  }

  /**
   * Centers the camera on a world position.
   *
   * @param target - the world pos to center on
   */
  public void snapTo(WorldPos target) {
    model.snapTo(target);

    double targetX = model.getX();
    double targetY = model.getY();

    // Stop any previous snap
    if (snapTimeline != null) {
      snapTimeline.stop();
    }

    // Short animation to new pos
    snapTimeline = new Timeline(
        new KeyFrame(
            Duration.millis(250),
            new KeyValue(translate.xProperty(), targetX, Interpolator.EASE_BOTH),
            new KeyValue(translate.yProperty(), targetY, Interpolator.EASE_BOTH)));
    snapTimeline.setOnFinished(e -> syncTransforms());

    snapTimeline.play();
  }

  /**
   * Gets the world position currently at the center of the screen
   *
   * @return the position of the world center
   */
  public WorldPos getWorldCenter() {
    return model.sceneToWorld(getSceneCenter());
  }

  private void syncTransforms() {
    translate.setX(model.getX());
    translate.setY(model.getY());

    scale.setX(model.getZoom());
    scale.setY(model.getZoom());
  }
}
