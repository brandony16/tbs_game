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

    private HexPos selectedPos;
    private Set<HexPos> reachableHexes = Set.of();

    private HexPos hoveredPos;
    private Consumer<HoverContext> onHoverChanged;

    private boolean isAnimating;

    private final WorldView left;
    private final WorldView center;
    private final WorldView right;

    public BoardView(Game game) {
        this.game = game;

        this.isAnimating = false;

        this.left = new WorldView(game);
        this.center = new WorldView(game);
        this.right = new WorldView(game);

        left.drawInitial(selectedPos, reachableHexes);
        center.drawInitial(selectedPos, reachableHexes);
        right.drawInitial(selectedPos, reachableHexes);

        double boardWidth = game.getBoard().getWidth() * HexMath.HEX_WIDTH;
        left.getRoot().setTranslateX(-boardWidth);
        right.getRoot().setTranslateX(boardWidth);

        worldRoot.getChildren().addAll(left.getRoot(), center.getRoot(), right.getRoot());
    }

    public void showCoords() {
        left.showCoords();
        center.showCoords();
        right.showCoords();
    }

    public void showSpawnScore() {
        left.showSpawnScore();
        center.showSpawnScore();
        right.showSpawnScore();
    }

    public void hideDebug() {
        left.hideDebug();
        center.hideDebug();
        right.hideDebug();
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
        left.drawInitial(selectedPos, reachableHexes);
        center.drawInitial(selectedPos, reachableHexes);
        right.drawInitial(selectedPos, reachableHexes);
    }

    public void redraw() {
        left.redraw(selectedPos, reachableHexes);
        center.redraw(selectedPos, reachableHexes);
        right.redraw(selectedPos, reachableHexes);
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
        Runnable onFinish = () -> {
            clearSelection();
            redraw();
            onTurnResolved.run();
            this.isAnimating = false;
        };

        SequentialTransition leftSequence = left.buildMoveAnimation(path, onFinish);
        SequentialTransition centerSequence = center.buildMoveAnimation(path, onFinish);
        SequentialTransition rightSequence = right.buildMoveAnimation(path, onFinish);

        this.isAnimating = true;
        leftSequence.play();
        centerSequence.play();
        rightSequence.play();
    }

    public void animateAIMove(Move move, Runnable onDone) {
        List<HexPos> path = move.path;

        Runnable onFinish = () -> {
            clearSelection();
            redraw();
            onTurnResolved.run();
            this.isAnimating = false;
            onDone.run();
        };

        SequentialTransition leftSequence = left.buildMoveAnimation(path, onFinish);
        SequentialTransition centerSequence = center.buildMoveAnimation(path, onFinish);
        SequentialTransition rightSequence = right.buildMoveAnimation(path, onFinish);

        this.isAnimating = true;
        leftSequence.play();
        centerSequence.play();
        rightSequence.play();
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
        return center.getRoot().sceneToLocal(mouseX, mouseY);
    }

    private HexPos getHexPosAt(double mouseX, double mouseY) {
        Point2D boardCoords = center.getRoot().sceneToLocal(mouseX, mouseY);
        return HexMath.pixelToHex(boardCoords.getX(), boardCoords.getY());
    }

    // Used for notifying when a turn's animation is over.
    private Runnable onTurnResolved = () -> {
    };

    public void setOnTurnResolved(Runnable callback) {
        this.onTurnResolved = callback;
    }
}
