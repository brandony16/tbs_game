package tbs_game.gui.board;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javafx.animation.SequentialTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import tbs_game.game.Game;
import tbs_game.game.Move;
import tbs_game.game.Movement;
import tbs_game.gui.ClickResult;
import tbs_game.gui.HexMath;
import tbs_game.gui.HoverContext;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public class BoardView {

    public static final int TILE_RADIUS = 64;

    private final Game game;

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

    private final Group tilingRoot = new Group();

    public BoardView(Game game) {
        this.game = game;

        this.isAnimating = false;

        this.boardLayer = new BoardLayer(game);
        this.highlightLayer = new HighlightLayer(game);
        this.unitLayer = new UnitLayer(game);
        this.debugLayer = new DebugLayer(game);

        setupTilingBoards();
        worldRoot.getChildren().addAll(tilingRoot, highlightLayer.getRoot(), unitLayer.getRoot(), debugLayer.getRoot());
    }

    private void setupTilingBoards() {
        for (int dx = -1; dx <= 1; dx++) {
            BoardLayer copy = new BoardLayer(game);
            copy.drawBoard();

            Group root = copy.getRoot();
            root.setTranslateX(dx * (root.getLayoutBounds().getWidth() - TILE_RADIUS));

            tilingRoot.getChildren().add(root);
        }
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

                List<HexPos> path = Movement.planMove(game.getState(), selectedPos, clicked).path;
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
        redraw();
    }

    private void clearSelection() {
        this.selectedPos = null;
        this.reachableHexes = Set.of();
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

    public void animateAIMove(Move move, Runnable onFinish) {
        List<HexPos> path = move.path;
        HexPos start = path.get(0);
        HexPos end = path.get(path.size() - 1);

        SequentialTransition sequence = unitLayer.buildMoveAnimation(path);

        sequence.setOnFinished(e -> {
            unitLayer.moveUnitElement(start, end);

            clearSelection();
            redraw();
            onTurnResolved.run();
            this.isAnimating = false;
            onFinish.run();
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
    public Point2D getLocalCoords(double mouseX, double mouseY) {
        return boardLayer.getRoot().sceneToLocal(mouseX, mouseY);
    }

    private HexPos getHexPosAt(double mouseX, double mouseY) {
        Point2D boardCoords = boardLayer.getRoot().sceneToLocal(mouseX, mouseY);
        return HexMath.pixelToHex(boardCoords.getX(), boardCoords.getY());
    }

    // Used for notifying when a turn's animation is over.
    private Runnable onTurnResolved = () -> {
    };

    public void setOnTurnResolved(Runnable callback) {
        this.onTurnResolved = callback;
    }
}
