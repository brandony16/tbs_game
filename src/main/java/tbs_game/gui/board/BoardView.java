package tbs_game.gui.board;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javafx.animation.SequentialTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import tbs_game.game.Game;
import tbs_game.gui.Camera;
import tbs_game.gui.ClickResult;
import tbs_game.gui.HexMath;
import tbs_game.gui.HoverContext;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public class BoardView {

    public static final int TILE_RADIUS = 40;

    private final Game game;
    private final Camera camera = new Camera();

    private final Group worldRoot = new Group();

    private final BoardLayer boardLayer;
    private final HighlightLayer highlightLayer;
    private final UnitLayer unitLayer;
    private final DebugLayer debugLayer;

    private HexPos selectedPos;
    private Set<HexPos> reachableHexes = Set.of();

    private HexPos hoveredPos;
    private Consumer<HoverContext> onHoverChanged;

    private boolean isAnimating;

    public BoardView(Game game) {
        this.game = game;
        this.isAnimating = false;

        this.boardLayer = new BoardLayer(game);
        this.highlightLayer = new HighlightLayer(game);
        this.unitLayer = new UnitLayer(game);
        this.debugLayer = new DebugLayer(game);

        worldRoot.getChildren().addAll(boardLayer.getRoot(), highlightLayer.getRoot(), unitLayer.getRoot(), debugLayer.getRoot());
    }

    public void showCoords() {
        debugLayer.drawCoords();
        debugLayer.show();
    }

    public void showSpawnScore() {
        debugLayer.drawSpawnScores();
        debugLayer.show();
    }

    public void hideDebug() {
        debugLayer.hide();
    }

    public Group getWorldRoot() {
        return worldRoot;
    }

    public Camera getCamera() {
        return camera;
    }

    public HexPos getSelected() {
        return selectedPos;
    }

    // ----- Drawing -----
    public void nextTurn() {
        clearSelection();
    }

    public void drawInitial() {
        boardLayer.drawBoard();
        highlightLayer.drawHighlights(selectedPos, reachableHexes);
        unitLayer.drawUnits();
    }

    public void redraw() {
        highlightLayer.drawHighlights(selectedPos, reachableHexes);
        unitLayer.drawUnits();
    }

    public void handleMouseMoved(double mouseX, double mouseY) {
        HexPos pos = getHexPosAt(mouseX, mouseY);

        if (!Objects.equals(pos, hoveredPos)) {
            hoveredPos = pos;
            notifyHoverChanged();
        }
    }

    // ----- Interaction -----
    public ClickResult handleClick(double mouseX, double mouseY) {
        if (isAnimating) {
            return ClickResult.NONE;
        }

        HexPos clicked = getHexPosAt(mouseX, mouseY);

        if (!game.getBoard().isOnBoard(clicked)) {
            clearSelection();
            return ClickResult.SELECTION_CHANGED;
        }

        Unit unit = game.getUnitAt(clicked);
        boolean isFriendlyUnit = unit != null && unit.getOwner().equals(game.getCurrentPlayer());

        if (selectedPos == null && isFriendlyUnit) {
            selectPos(clicked);
            return ClickResult.SELECTION_CHANGED;
        }

        if (selectedPos != null) {
            if (reachableHexes.contains(clicked)) {
                if (!game.resolveAction(selectedPos, clicked)) {
                    return ClickResult.NONE;
                }

                List<HexPos> path = game.getMoveCache().get(selectedPos, clicked).path;
                animateMove(path);
                return ClickResult.MOVE_STARTED;
            }

            if (isFriendlyUnit) {
                selectPos(clicked);
                return ClickResult.SELECTION_CHANGED;
            }

            clearSelection();
            return ClickResult.SELECTION_CHANGED;
        }
        return ClickResult.NONE;
    }

    private void selectPos(HexPos pos) {
        this.selectedPos = pos;
        this.reachableHexes = game.getReachableHexes(pos);
        this.reachableHexes.add(pos); // Include current pos
        game.getMoveCache().clear();
        redraw();
    }

    private void clearSelection() {
        this.selectedPos = null;
        this.reachableHexes = Set.of();
        game.getMoveCache().clear();
        redraw();
    }

    private void animateMove(List<HexPos> path) {
        HexPos start = path.get(0);
        HexPos end = path.get(path.size() - 1);

        SequentialTransition sequence = unitLayer.buildMoveAnimation(path);

        sequence.setOnFinished(e -> {
            unitLayer.moveUnitElement(start, end);

            clearSelection();
            redraw();
            onTurnResolved.run();
            this.isAnimating = false;
        });

        this.isAnimating = true;
        sequence.play();
    }

    public void setOnHoverChanged(Consumer<HoverContext> handler) {
        this.onHoverChanged = handler;
    }

    private void notifyHoverChanged() {
        if (onHoverChanged == null) {
            return;
        }

        if (selectedPos == null || hoveredPos == null) {
            onHoverChanged.accept(null);
            return;
        }

        boolean canAttack = game.canAttack(selectedPos, hoveredPos);

        onHoverChanged.accept(
                new HoverContext(selectedPos, hoveredPos, canAttack)
        );
    }

    // ----- Utility -----
    private HexPos getHexPosAt(double mouseX, double mouseY) {
        Point2D boardCoords = boardLayer.getRoot().sceneToLocal(mouseX, mouseY);
        Point2D adjustedCoords = camera.screenToWorld(boardCoords.getX(), boardCoords.getY());
        return HexMath.pixelToHex(adjustedCoords.getX(), adjustedCoords.getY());
    }

    // Used for notifying when a turn's animation is over.
    private Runnable onTurnResolved = () -> {
    };

    public void setOnTurnResolved(Runnable callback) {
        this.onTurnResolved = callback;
    }
}
