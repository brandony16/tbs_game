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

  // Hard bounds on zooms to prevent extreme scales
  private static final double MIN_ZOOM = 0.5;
  private static final double MAX_ZOOM = 4.0;

  // Width of the world in world units (used for horizontal wrapping)
  private final double boardWidthPx;

  // Scene dimensions in scene coordinates
  private double sceneWidth;
  private double sceneHeight;

  private final Scale scale = new Scale(1, 1);
  private final Translate translate = new Translate(0, 0);

  private Timeline snapTimeline;

  public Camera(double boardWidthPx) {
    this.boardWidthPx = boardWidthPx;
  }

  /**
   * Sets the scene size of the camera. Should generally only be called once for
   * setup, after the root is attached to the scene.
   * 
   * @param w - width in pixels
   * @param h - height in pixels
   */
  public void setSceneSize(double w, double h) {
    this.sceneWidth = w;
    this.sceneHeight = h;
  }

  /**
   * Updates the width of the scene. Adjusts translation to keep items centered
   * 
   * @param newWidth - the new width of the scene
   */
  public void updateSceneWidth(double newWidth) {
    double diff = newWidth - sceneWidth;

    SceneDelta adjustment = new SceneDelta(-diff / 2, 0);
    panScene(adjustment);

    this.sceneWidth = newWidth;
  }

  /**
   * Updates the height of the scene. Adjusts translation to keep items centered
   * 
   * @param newHeight - the new height of the scene
   */
  public void updateSceneHeight(double newHeight) {
    double diff = newHeight - sceneHeight;

    SceneDelta adjustment = new SceneDelta(0, -diff / 2);
    panScene(adjustment);

    this.sceneHeight = newHeight;
  }

  /**
   * Center of the scene in scene coordinates. Not affected by camera transforms.
   * 
   * @return - ScenePos of the center coords
   */
  public ScenePos getSceneCenter() {
    return new ScenePos(sceneWidth / 2.0, sceneHeight / 2.0);
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
    return new ScenePos(translate.getX(), translate.getY());
  }

  /**
   * Gets the zoom factor of the camera
   * 
   * @return - zoom factor
   */
  public double getZoom() {
    return this.scale.getX();
  }

  /**
   * Zooms by a factor around the origin.
   * 
   * @param factor - factor to zoom by. < 1 for zoom out, > 1 for zoom in
   */
  public void zoom(double factor) {
    scale.setX(clamp(scale.getX() * factor));
    scale.setY(clamp(scale.getY() * factor));
  }

  /**
   * Zooms by a factor while keeping the pivot point fixed.
   * 
   * @param zoomFactor - factor to zoom by. < 1 for zoom out, > 1 for zoom in
   * @param pivot      - the point to keep fixed
   */
  public void zoomAt(double zoomFactor, ScenePos pivot) {
    double oldZoom = scale.getX();
    double newZoom = clamp(oldZoom * zoomFactor);

    double zoomRatio = newZoom / oldZoom;

    /*
     * To zoom at a pivot:
     * pivotScene = world * oldZoom + oldTranslate
     * pivotScene = world * newZoom + newTranslate
     * 
     * Solve for newTranslate so pivotScene does not move.
     * oldTranslate - (pivotScene - oldTranslate) * (newZoom / oldZoom - 1)
     */
    translate.setX(translate.getX() - (pivot.x() - translate.getX()) * (zoomRatio - 1));
    translate.setY(translate.getY() - (pivot.y() - translate.getY()) * (zoomRatio - 1));

    // Apply new scale
    scale.setX(newZoom);
    scale.setY(newZoom);
  }

  /**
   * Pans the scene using a scene pixel delta.
   * 
   * @param delta - the delta containing the increments to pan by
   */
  public void panScene(SceneDelta delta) {
    // Move world opposite mouse movement
    double newX = translate.getX() - delta.dx();
    translate.setY(translate.getY() - delta.dy()); // Y doesn't wrap

    // Bound on which wrapping should occur (in world space)
    double halfWidth = boardWidthPx / 2.0;

    // compute world space center
    double sceneCenterX = sceneWidth / 2.0;
    double worldCenterX = (sceneCenterX - translate.getX()) / scale.getX();

    // If center shifts across the edge of the board, shift the board by a board
    // width
    if (worldCenterX > halfWidth) {
      translate.setX(newX + boardWidthPx * scale.getX());
    } else if (worldCenterX < -halfWidth) {
      translate.setX(newX - boardWidthPx * scale.getX());
    } else {
      translate.setX(newX);
    }
  }

  private double clamp(double z) {
    return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, z));
  }

  /**
   * Centers the camera on a world position.
   * 
   * @param target - the world pos to center on
   */
  public void snapTo(WorldPos target) {
    ScenePos sceneCenter = getSceneCenter();

    double targetX = sceneCenter.x() - target.x() * scale.getX();
    double targetY = sceneCenter.y() - target.y() * scale.getY();

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

    snapTimeline.play();
  }

  /**
   * Sets translate values to whole pixels
   */
  public void snapToPixelGrid() {
    translate.setX(Math.round(translate.getX()));
    translate.setY(Math.round(translate.getY()));
  }

  /**
   * Converts a point from scene space to world space
   * 
   * @param p - point in scene space
   * @return - the posistion in world space
   */
  public WorldPos sceneToWorld(ScenePos p) {
    double wx = (p.x() - translate.getX()) / scale.getX();
    double wy = (p.y() - translate.getY()) / scale.getY();
    return new WorldPos(wx, wy);
  }

  /**
   * Gets the world position currently at the center of the screen
   * 
   * @return
   */
  public WorldPos getWorldCenter() {
    return sceneToWorld(getSceneCenter());
  }
}
