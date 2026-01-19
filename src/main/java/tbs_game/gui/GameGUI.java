package tbs_game.gui;

import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import tbs_game.game.ActionPath;
import tbs_game.game.Game;
import tbs_game.gui.board.BoardView;
import tbs_game.gui.camera.Camera;
import tbs_game.hexes.AxialPos;

public class GameGUI {

    private double sceneWidth;
    private double sceneHeight;

    private final Game game;
    private final BoardView boardView;
    private final HudView hudView;

    private final StackPane root;

    private final Camera camera;
    private Point2D lastMousePos;

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
        root.setOnMousePressed(e -> lastMousePos = new Point2D(e.getX(), e.getY()));
        root.setOnMouseDragged(e -> {
            Point2D now = new Point2D(e.getX(), e.getY());
            Point2D delta = now.subtract(lastMousePos);

            camera.pan(-delta.getX(), -delta.getY());

            lastMousePos = now;
            applyCamera();
        });
        root.setOnScroll(e -> {
            double factor = e.getDeltaY() > 0 ? 1.1111 : 0.9;

            camera.zoom(factor);

            applyCamera();
        });

        hudView.initHUD();
        boardView.drawInitial();
        hudView.updateHUD(boardView.getSelected());
        camera.setScreenSize(sceneWidth, sceneHeight);
        // snapCameraToUnit();
    }

    private void applyCamera() {

        Node boardRoot = boardView.getWorldRoot();

        double z = camera.getZoom();
        boardRoot.setScaleX(z);
        boardRoot.setScaleY(z);

        boardRoot.setTranslateX(-sceneWidth / 2 + camera.getCenterX() * z);
        boardRoot.setTranslateY(-sceneHeight / 2 + camera.getCenterY() * z);
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

    private void snapCameraToUnit() {
        List<AxialPos> unitPositions = game.getPositionsForPlayer(game.getCurrentPlayer());

        AxialPos unitPos = unitPositions.get(0);
        double wx = HexMath.axialToPixelX(unitPos);
        double wy = HexMath.axialToPixelY(unitPos);

        double startCenterX = camera.getCenterX();
        double startCenterY = camera.getCenterY();
        double startZoom = camera.getZoom();

        // setting offsets to (wx, wy) puts those in the top left corner
        // want (wx, wy) to be in the center of the screen
        double targetCenterX = wx;
        double targetCenterY = wy;
        double targetZoom = 1.0;

        // Short animation 
        Duration duration = Duration.millis(220);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(camera.centerXProperty(), startCenterX),
                        new KeyValue(camera.centerYProperty(), startCenterY),
                        new KeyValue(camera.zoomProperty(), startZoom)
                ),
                new KeyFrame(duration,
                        new KeyValue(camera.centerXProperty(), targetCenterX, Interpolator.EASE_BOTH),
                        new KeyValue(camera.centerYProperty(), targetCenterY, Interpolator.EASE_BOTH),
                        new KeyValue(camera.zoomProperty(), targetZoom, Interpolator.EASE_OUT)
                )
        );

        timeline.currentTimeProperty().addListener((obs, oldT, newT) -> {
            applyCamera();
        });

        timeline.setOnFinished(e -> {
            camera.snapToPixelGrid();
            applyCamera();
        });

        timeline.play();
        boardView.setSelected(unitPos);
    }

    public void setSceneWidth(double width) {
        double diff = width - sceneWidth;
        this.sceneWidth = width;

        camera.setScreenSize(sceneWidth, sceneHeight);
        camera.pan(diff / 2, 0);
        applyCamera();
    }

    public void setSceneHeight(double height) {
        double diff = height - sceneHeight;
        this.sceneHeight = height;

        camera.setScreenSize(sceneWidth, sceneHeight);
        camera.pan(0, diff / 2);
        applyCamera();
    }
}
