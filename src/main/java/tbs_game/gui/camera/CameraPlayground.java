package tbs_game.gui.camera;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import tbs_game.gui.coord_systems.SceneDelta;
import tbs_game.gui.coord_systems.ScenePos;
import tbs_game.gui.coord_systems.WorldPos;

public class CameraPlayground {

  private final Camera cam;

  private Pane root;
  private Group world;

  private ScenePos lastMousePos;

  private Circle origin;
  private Rectangle r1;
  private Rectangle r2;
  private Circle c;

  public CameraPlayground() {
    this(4000);
  }

  public CameraPlayground(double width) {
    this.cam = new Camera(width);
  }

  public void setSceneSize(double w, double h) {
    cam.setSceneSize(w, h);
  }

  public void updateSceneWidth(double w) {
    cam.updateSceneWidth(w);
  }

  public void updateSceneHeight(double h) {
    cam.updateSceneHeight(h);
  }

  public Pane getRoot() {
    return root;
  }

  public void setUpBasic() {
    root = new Pane();
    world = new Group();

    root.getChildren().add(world);
    cam.attach(world);

    drawGrid(50, 2000);
    drawAxes();
    drawTestShapes();

    root.setOnMousePressed(e -> lastMousePos = new ScenePos(e.getSceneX(), e.getSceneY()));
    root.setOnMouseDragged(e -> {
      ScenePos now = new ScenePos(e.getSceneX(), e.getSceneY());
      SceneDelta delta = lastMousePos.subtract(now);

      cam.panScene(delta);

      lastMousePos = now;
    });
    root.setOnScroll(e -> {
      double factor = e.getDeltaY() > 0 ? 1.1111 : 0.9;
      ScenePos pivot = new ScenePos(e.getSceneX(), e.getSceneY());

      cam.zoomAt(factor, pivot);
    });

    root.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case DIGIT1 -> cam.snapTo(centerOf(origin)); // origin
        case DIGIT2 -> cam.snapTo(centerOf(r1)); // rect 1
        case DIGIT3 -> cam.snapTo(centerOf(r2)); // rect 2
        case DIGIT4 -> cam.snapTo(centerOf(c)); // circle
      }
    });
  }

  // ---------- Helpers ----------
  private void drawGrid(double spacing, double size) {
    for (double x = -size; x <= size; x += spacing) {
      Line line = new Line(x, -size, x, size);
      line.setStroke(Color.LIGHTGRAY);
      world.getChildren().add(line);
    }

    for (double y = -size; y <= size; y += spacing) {
      Line line = new Line(-size, y, size, y);
      line.setStroke(Color.LIGHTGRAY);
      world.getChildren().add(line);
    }
  }

  private void drawAxes() {
    Line xAxis = new Line(-2000, 0, 2000, 0);
    xAxis.setStroke(Color.RED);
    xAxis.setStrokeWidth(2);

    Line yAxis = new Line(0, -2000, 0, 2000);
    yAxis.setStroke(Color.BLUE);
    yAxis.setStrokeWidth(2);

    world.getChildren().addAll(xAxis, yAxis);
  }

  private void drawTestShapes() {
    // Origin marker
    this.origin = new Circle(0, 0, 6, Color.BLACK);

    // Reference rectangles at known coordinates
    this.r1 = new Rectangle(100, 100, 80, 60);
    r1.setFill(Color.ORANGE);

    this.r2 = new Rectangle(-200, -150, 120, 90);
    r2.setFill(Color.GREEN);

    this.c = new Circle(300, -200, 40);
    c.setFill(Color.PURPLE);

    world.getChildren().addAll(origin, r1, r2, c);
  }

  WorldPos centerOf(Rectangle r) {
    return new WorldPos(
        r.getX() + r.getWidth() / 2.0,
        r.getY() + r.getHeight() / 2.0);
  }

  WorldPos centerOf(Circle c) {
    return new WorldPos(c.getCenterX(), c.getCenterY());
  }
}
