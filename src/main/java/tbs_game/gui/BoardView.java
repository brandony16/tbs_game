package tbs_game.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tbs_game.HexPos;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.units.Unit;

public class BoardView {

    private static final int TILE_RADIUS = 40;
    private static final Color DEFAULT_TILE_COLOR = Color.GREEN;
    private final HexMath hexMath = new HexMath(TILE_RADIUS);

    private final Game game;
    private final Camera camera = new Camera();

    private final Group worldRoot = new Group(); // all board content
    private final Group boardGroup = new Group();
    private final Group highlightGroup = new Group();
    private final Group unitGroup = new Group();
    private final Map<HexPos, Group> unitElements = new HashMap<>();

    private HexPos selectedPos;
    private Set<HexPos> reachableHexes = Set.of();

    private HexPos hoveredPos;
    private Consumer<HoverContext> onHoverChanged;

    public BoardView(Game game) {
        this.game = game;
        worldRoot.getChildren().addAll(boardGroup, highlightGroup, unitGroup);
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

    public void redraw() {
        drawBoard();
        drawUnits();
    }

    public void handleMouseMoved(double mouseX, double mouseY) {
        HexPos pos = getHexPosAt(mouseX, mouseY);

        if (!Objects.equals(pos, hoveredPos)) {
            hoveredPos = pos;
            notifyHoverChanged();
        }
    }

    private void drawBoard() {
        boardGroup.getChildren().clear();
        highlightGroup.getChildren().clear();

        Board board = game.getBoard();

        for (HexPos pos : board.getPositions()) {
            double cx = hexMath.hexToPixelX(pos);
            double cy = hexMath.hexToPixelY(pos);

            Polygon hex = createHex(cx, cy);
            hex.setFill(DEFAULT_TILE_COLOR);
            hex.setStroke(Color.BLACK);

            Text text = new Text();
            text.setText(pos.q() + " , " + pos.r());
            text.setX(cx - 10);
            text.setY(cy);

            boardGroup.getChildren().addAll(hex, text);

            if (selectedPos != null) {
                if (selectedPos.equals(pos)) {
                    Polygon outline = createHex(cx, cy);
                    outline.setFill(Color.TRANSPARENT);
                    outline.setStroke(Color.GOLD);
                    outline.setStrokeWidth(4);
                    outline.setStrokeType(StrokeType.INSIDE);
                    outline.setMouseTransparent(true);
                    highlightGroup.getChildren().add(outline);
                } else if (reachableHexes.contains(pos)) {
                    Polygon highlight = createHex(cx, cy);
                    highlight.setFill(Color.rgb(0, 0, 0, 0.3));
                    Unit selectedUnit = game.getUnitAt(selectedPos);
                    Unit unitInRange = game.getUnitAt(pos);
                    if (unitInRange != null && !selectedUnit.getOwner().equals(unitInRange.getOwner())) {
                        highlight.setStroke(Color.RED);
                        highlight.setStrokeWidth(2);
                        highlight.setStrokeType(StrokeType.INSIDE);
                    }
                    highlightGroup.getChildren().add(highlight);
                }
            }
        }
    }

    private void drawUnits() {
        unitGroup.getChildren().clear();
        unitElements.clear();

        for (HexPos pos : game.getUnitPositions()) {
            Group unitElement = drawUnitElement(pos);
            unitElements.put(pos, unitElement);
            unitGroup.getChildren().add(unitElement);
        }
    }

    private Group drawUnitElement(HexPos pos) {
        double cx = hexMath.hexToPixelX(pos);
        double cy = hexMath.hexToPixelY(pos);

        Group group = new Group();
        Unit unit = game.getUnitAt(pos);

        Circle body = new Circle(cx, cy, TILE_RADIUS * 0.35);
        body.setFill(unit.getOwner() == tbs_game.player.Player.USER ? Color.BLUE : Color.RED);
        body.setStroke(Color.BLACK);

        double barWidth = TILE_RADIUS * 0.6;
        double barHeight = 6;
        double barX = cx - barWidth / 2;
        double barY = cy + TILE_RADIUS * 0.45;

        Rectangle bg = new Rectangle(barX, barY, barWidth, barHeight);
        bg.setFill(Color.DARKRED);

        double hpRatio = (double) unit.getHealth() / unit.getType().maxHp;
        Rectangle fg = new Rectangle(barX, barY, barWidth * hpRatio, barHeight);
        fg.setFill(Color.LIMEGREEN);

        group.getChildren().addAll(body, bg, fg);
        return group;
    }

    // ----- Interaction -----
    public ClickResult handleClick(double mouseX, double mouseY) {
        HexPos clicked = getHexPosAt(mouseX, mouseY);
        Point2D boardCoords = boardGroup.sceneToLocal(mouseX, mouseY);
        System.out.println("BOARD COORDS:" + boardCoords.getX() + " , " + boardCoords.getY());
        System.out.println("ADJUSTED: " + clicked.q() + " , " + clicked.r());

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
                animateMove(selectedPos, clicked);
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
        redraw();
    }

    private void clearSelection() {
        this.selectedPos = null;
        this.reachableHexes = Set.of();
        redraw();
    }

    private void animateMove(HexPos from, HexPos to) {
        Group unitElement = unitElements.get(from);
        if (unitElement == null || !game.isValidMove(from, to)) {
            return;
        }

        double startX = hexMath.hexToPixelX(from);
        double startY = hexMath.hexToPixelY(from);
        double endX = hexMath.hexToPixelX(to);
        double endY = hexMath.hexToPixelY(to);

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), unitElement);
        tt.setFromX(0);
        tt.setFromY(0);
        tt.setToX(endX - startX);
        tt.setToY(endY - startY);
        tt.setOnFinished(e -> {
            unitElement.setTranslateX(0);
            unitElement.setTranslateY(0);
            unitElement.setLayoutX(endX);
            unitElement.setLayoutY(endY);

            unitElements.remove(from);
            unitElements.put(to, unitElement);

            // Update game state after animation
            game.moveUnit(from, to);
            clearSelection();
            redraw();

            onTurnResolved.run();
        });
        tt.play();
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

    private Polygon createHex(double cx, double cy) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30); // pointy-top
            double x = cx + TILE_RADIUS * Math.cos(angle);
            double y = cy + TILE_RADIUS * Math.sin(angle);
            hex.getPoints().addAll(x, y);
        }
        return hex;
    }

    private HexPos getHexPosAt(double mouseX, double mouseY) {
        Point2D boardCoords = boardGroup.sceneToLocal(mouseX, mouseY);
        Point2D adjustedCoords = camera.screenToWorld(boardCoords.getX(), boardCoords.getY());
        return hexMath.pixelToHex(adjustedCoords.getX(), adjustedCoords.getY());
    }

    // Used for notifying when a turn's animation is over.
    private Runnable onTurnResolved = () -> {
    };

    public void setOnTurnResolved(Runnable callback) {
        this.onTurnResolved = callback;
    }
}
