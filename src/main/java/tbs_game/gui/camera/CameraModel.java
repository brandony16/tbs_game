package tbs_game.gui.camera;

import tbs_game.gui.coord_systems.SceneDelta;
import tbs_game.gui.coord_systems.ScenePos;
import tbs_game.gui.coord_systems.WorldPos;

public class CameraModel {
  private double x = 0;
  private double y = 0;
  private double zoom = 1.0;

  private final double boardWidth;
  private double sceneWidth;
  private double sceneHeight;

  private static final double MIN_ZOOM = 0.5;
  private static final double MAX_ZOOM = 4.0;

  public CameraModel(double boardWidth) {
    this.boardWidth = boardWidth;
  }

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
   * Center of the scene in scene coordinates. Not affected by camera
   * transforms.
   *
   * @return - ScenePos of the center coords
   */
  public ScenePos getSceneCenter() {
    return new ScenePos(sceneWidth / 2.0, sceneHeight / 2.0);
  }

  /**
   * Zooms by a factor around the origin.
   *
   * @param factor - factor to zoom by. < 1 for zoom out, > 1 for zoom in
   */
  public void zoom(double factor) {
    zoom = clamp(zoom * factor);
  }

  /**
   * Zooms by a factor while keeping the pivot point fixed.
   *
   * @param zoomFactor - factor to zoom by. < 1 for zoom out, > 1 for zoom in
   * @param pivot      - the point to keep fixed
   */
  public void zoomAt(double zoomFactor, ScenePos pivot) {
    double oldZoom = zoom;
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
    this.x = x - (pivot.x() - x) * (zoomRatio - 1);
    this.y = y - (pivot.y() - y) * (zoomRatio - 1);

    // Apply new scale
    this.zoom = newZoom;
  }

  /**
   * Pans the scene using a scene pixel delta.
   *
   * @param delta - the delta containing the increments to pan by
   */
  public void panScene(SceneDelta delta) {
    this.y = y - delta.dy(); // Y doesn't wrap

    // Move world opposite mouse movement
    double newX = x - delta.dx();

    // Bound on which wrapping should occur (in world space)
    double halfWidth = boardWidth / 2.0;

    // compute world space center
    double sceneCenterX = sceneWidth / 2.0;
    double worldCenterX = (sceneCenterX - newX) / zoom;

    // If center shifts across the edge of the board, shift the board by a board
    // width
    if (worldCenterX > halfWidth) {
      this.x = newX + boardWidth * zoom;
    } else if (worldCenterX < -halfWidth) {
      this.x = newX - boardWidth * zoom;
    } else {
      this.x = newX;
    }
  }

  /**
   * Centers the camera on a world position.
   *
   * @param target - the world pos to center on
   */
  public void snapTo(WorldPos target) {
    ScenePos sceneCenter = getSceneCenter();

    this.x = sceneCenter.x() - target.x() * zoom;
    this.y = sceneCenter.y() - target.y() * zoom;
  }

  /**
   * Converts a point from scene space to world space
   *
   * @param p - point in scene space
   * @return - the posistion in world space
   */
  public WorldPos sceneToWorld(ScenePos p) {
    double wx = (p.x() - x) / zoom;
    double wy = (p.y() - y) / zoom;
    return new WorldPos(wx, wy);
  }

  private double clamp(double z) {
    return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, z));
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZoom() {
    return zoom;
  }
}
