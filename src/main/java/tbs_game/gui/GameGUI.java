package tbs_game.gui;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import tbs_game.game.ActionPath;
import tbs_game.game.Game;
import tbs_game.gui.board.BoardView;
import tbs_game.hexes.AxialPos;

public class GameGUI {

    private final Game game;
    private final BoardView boardView;
    private final HudView hudView;

    private final StackPane root;

    private final Camera camera;
    private Point2D lastMousePos;

    public GameGUI(Game game) {
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

            camera.pan(delta.getX(), delta.getY());
            lastMousePos = now;

            updateBoard();
        });
        root.setOnScroll(e -> {
            boolean zoomIn = e.getDeltaY() > 0;
            double factor = zoomIn ? 1.1 : 0.9;

            Point2D before = boardView.getLocalCoords(e.getX(), e.getY());
            camera.zoom(factor);
            updateBoard();

            Point2D after = boardView.getLocalCoords(e.getX(), e.getY());
            Point2D delta = after.subtract(before);

            if (zoomIn) {
                camera.pan(delta.getX(), delta.getY());
            }
            updateBoard();
        });

        hudView.initHUD();
        boardView.drawInitial();
        hudView.updateHUD(boardView.getSelected());
        snapCameraToUnit();
    }

    private void updateBoard() {
        Node boardRoot = boardView.getWorldRoot();

        double z = camera.getZoom();

        boardRoot.setScaleX(z);
        boardRoot.setScaleY(z);
        boardRoot.setTranslateX(-camera.getX() * z);
        boardRoot.setTranslateY(-camera.getY() * z);
    }

    public StackPane getRoot() {
        return root;
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

        camera.setCenter(wx, wy, 1960, 1080); // CHANGE 
        updateBoard();
    }
}
