package tbs_game.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.game.SetupHandler;
import tbs_game.hexes.FractionalHex;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public class BoardView {

    private static final int TILE_RADIUS = 40;
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

    private boolean showTileCoords;
    private boolean showSpawnScore;
    private boolean isAnimating;

    public BoardView(Game game) {
        this.game = game;
        this.showTileCoords = true;
        this.showSpawnScore = false;
        this.isAnimating = false;
        worldRoot.getChildren().addAll(boardGroup, highlightGroup, unitGroup);
    }

    public void showDebug() {
        this.showTileCoords = true;
        this.showSpawnScore = true;
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
            hex.setFill(board.getTile(pos).getTerrain().color);
            hex.setStroke(Color.BLACK);
            boardGroup.getChildren().add(hex);

            if (showTileCoords) {
                Text coord = getTileCoord(pos, cx, cy);
                boardGroup.getChildren().add(coord);
            } else if (showSpawnScore) {
                Text spawnScore = getSpawnScore(pos, cx, cy);
                boardGroup.getChildren().add(spawnScore);
            }

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

                    Point2D[] corners = hexCorners(cx, cy);

                    for (int edge = 0; edge < 6; edge++) {
                        HexPos neighbor = pos.neighbor(edge);

                        if (reachableHexes.contains(neighbor)) {
                            continue; // interior edge
                        }

                        Point2D a = corners[edge];
                        Point2D b = corners[(edge + 1) % 6];

                        Line line = new Line(a.getX(), a.getY(), b.getX(), b.getY());
                        line.setStroke(Color.LIGHTBLUE);
                        line.setStrokeWidth(4);
                        line.setStrokeLineCap(StrokeLineCap.ROUND);

                        highlightGroup.getChildren().add(line);
                    }
                }
            }
        }
    }

    private void debug() {
        // Throw somewhere to debug something when necessary
    }

    private Point2D[] hexCorners(double cx, double cy) {
        Point2D[] corners = new Point2D[6];
        for (int i = 0; i < 6; i++) {
            double angleRad = Math.toRadians(60 * i - 30);
            double x = cx + TILE_RADIUS * Math.cos(angleRad);
            double y = cy - TILE_RADIUS * Math.sin(angleRad);
            corners[i] = new Point2D(x, y);
        }
        return corners;
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
        body.setFill(unit.getOwner().color);
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

    private Text getTileCoord(HexPos pos, double cx, double cy) {
        Text coord = new Text();
        coord.setText(pos.q() + " , " + pos.r());
        coord.setX(cx - 10);
        coord.setY(cy);

        return coord;
    }

    private Text getSpawnScore(HexPos pos, double cx, double cy) {
        Text score = new Text();
        int spawnScore = SetupHandler.getSpawnScore(pos, game.getBoard());
        if (spawnScore == Integer.MIN_VALUE) {
            return score;
        }

        score.setText("" + spawnScore);
        score.setX(cx - 10);
        score.setY(cy);

        return score;
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

                List<HexPos> path = FractionalHex.hexLinedraw(selectedPos, clicked);
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
        Group unitElement = unitElements.get(start);

        SequentialTransition sequence = new SequentialTransition(unitElement);

        for (int i = 1; i < path.size(); i++) {
            HexPos a = path.get(i - 1);
            HexPos b = path.get(i);

            double dx = hexMath.hexToPixelX(b) - hexMath.hexToPixelX(a);
            double dy = hexMath.hexToPixelY(b) - hexMath.hexToPixelY(a);

            TranslateTransition tt = new TranslateTransition(Duration.millis(200));
            tt.setByX(dx);
            tt.setByY(dy);

            sequence.getChildren().add(tt);
        }

        sequence.setOnFinished(e -> {
            unitElements.remove(start);
            unitElements.put(end, unitElement);

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
