package tbs_game.gui;

import java.util.List;

import javafx.scene.layout.StackPane;
import tbs_game.game.ActionPath;
import tbs_game.game.Game;
import tbs_game.gui.board.BoardView;
import tbs_game.gui.camera.Camera;
import tbs_game.gui.coord_systems.SceneDelta;
import tbs_game.gui.coord_systems.ScenePos;
import tbs_game.gui.coord_systems.WorldPos;
import tbs_game.gui.hud.HudView;
import tbs_game.hexes.AxialPos;

public class GameGUI {

  private double sceneWidth;
  private double sceneHeight;

  private final Game game;
  private final BoardView boardView;
  private final HudView hudView;

  private final StackPane root;

  private final Camera camera;
  private ScenePos lastMousePos;

  public GameGUI(Game game, double defaultWidth, double defaultHeight) {
    this.sceneWidth = defaultWidth;
    this.sceneHeight = defaultHeight;

    this.game = game;

    double boardWidth = game.getBoard().getWidth() * HexMath.HEX_WIDTH;
    this.camera = new Camera(boardWidth);

    this.boardView = new BoardView(game);
    this.hudView = new HudView(game);

    this.root = new StackPane();

    root.getChildren().addAll(boardView.getWorldRoot(), hudView.getHudRoot());

    hudView.setOnEndTurn(() -> endTurn());

    boardView.setOnTurnResolved(() -> {
      hudView.updateHUD(boardView.getSelected());
      hudView.hideCombatPreview();
    });

    boardView.setOnHoverChanged(ctx -> {
      if (ctx != null) {
        hudView.showCombatPreview(ctx);
      }
    });

    root.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));
    root.setOnMouseMoved(e -> boardView.handleMouseMoved(e.getX(), e.getY()));

    // Panning and Zooming
    root.setOnMousePressed(e -> lastMousePos = new ScenePos(e.getSceneX(), e.getSceneY()));
    root.setOnMouseDragged(e -> {
      ScenePos now = new ScenePos(e.getSceneX(), e.getSceneY());
      SceneDelta delta = lastMousePos.subtract(now);

      camera.panScene(delta);

      lastMousePos = now;
    });
    root.setOnScroll(e -> {
      double factor = e.getDeltaY() > 0 ? 1.1111 : 0.9;
      ScenePos pivot = new ScenePos(e.getSceneX(), e.getSceneY());

      camera.zoomAt(factor, pivot);
    });

    hudView.initHUD();
    hudView.updateHUD(boardView.getSelected());
    camera.setSceneSize(sceneWidth, sceneHeight);
    camera.attach(boardView.getWorldRoot());
    snapCameraToUnit();
  }

  public StackPane getRoot() {
    return this.root;
  }

  public void animateAIMove(ActionPath move, Runnable onFinish) {
    this.boardView.animateAIMove(move, onFinish);
  }

  public void updateHUD() {
    hudView.updateHUD(boardView.getSelected());
    if (game.isUsersTurn()) {
      snapCameraToUnit();
    }
  }

  private void handleClick(double mouseX, double mouseY) {
    if (!game.getActionQueue().isEmpty() || game.getCurrentPlayer().isAI()) {
      return;
    }

    ClickResult result = boardView.handleClick(mouseX, mouseY);

    if (result == ClickResult.SELECTION_CHANGED) {
      hudView.updateHUD(boardView.getSelected());
    }
  }

  private void endTurn() {
    if (!game.canEndTurn()) {
      return;
    }

    game.endTurn();
    boardView.nextTurn();
    hudView.updateHUD(null);
    hudView.hideCombatPreview();
  }

  // Snaps the camera to a unit
  private void snapCameraToUnit() {
    List<AxialPos> unitPositions = game.getPositionsForPlayer(game.getCurrentPlayer());

    AxialPos unitPos = unitPositions.get(0);
    WorldPos worldPos = HexMath.axialToWorldPos(unitPos);

    camera.snapTo(worldPos);

    boardView.setSelected(unitPos);
  }

  // Updates the camera scene dimensions
  public void updateSceneWidth(double width) {
    camera.updateSceneWidth(width);
  }

  public void updateSceneHeight(double height) {
    camera.updateSceneHeight(height);
  }
}
