package tbs_game.gui;

import java.util.Set;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import tbs_game.HexPos;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.units.Unit;

public class GameGUI {

    private static final int TILE_RADIUS = 40;
    private static final double SQRT3 = Math.sqrt(3);

    private final Pane root;
    private final Game game;
    private final Group boardGroup;
    private final Group highlightGroup;
    private final Group unitGroup;

    private HexPos selectedPos;
    private Set<HexPos> reachableHexes = Set.of();

    public GameGUI(Game game) {
        this.game = game;
        this.root = new Pane();
        this.boardGroup = new Group();
        this.highlightGroup = new Group();
        this.unitGroup = new Group();
        root.getChildren().addAll(boardGroup, highlightGroup, unitGroup);

        root.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));

        redraw();
    }

    public Pane getRoot() {
        return root;
    }

    private void handleClick(double mouseX, double mouseY) {
        // Convert to "board space" w/ (0,0) being the center of the board
        double localX = mouseX - boardGroup.getLayoutX();
        double localY = mouseY - boardGroup.getLayoutY();

        HexPos clicked = pixelToHex(localX, localY);
        if (!game.getBoard().isOnBoard(clicked)) {
            clearSelection();
            return;
        }

        Unit unit = game.getUnitAt(clicked);
        boolean isFriendlyUnit = unit != null && unit.getOwner().equals(game.getCurrentPlayer());
        if (this.selectedPos == null) {
            if (isFriendlyUnit) {
                selectPos(clicked);
            }
        } else {
            if (reachableHexes.contains(clicked)) {
                game.moveUnit(selectedPos, clicked);
                clearSelection();
                redraw();
            } else if (isFriendlyUnit) {
                selectPos(clicked); // switch selection
            } else {
                clearSelection();
            }
        }
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

    private void drawBoard() {
        boardGroup.getChildren().clear();
        highlightGroup.getChildren().clear();

        Board board = game.getBoard();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (HexPos pos : board.getPositions()) {
            double cx = hexToPixelX(pos);
            double cy = hexToPixelY(pos);

            Polygon hex = createHex(cx, cy);
            hex.setFill(Color.GREEN);
            hex.setStroke(Color.BLACK);
            boardGroup.getChildren().add(hex);

            if (selectedPos != null) {
                if (selectedPos.equals(pos)) { // Gold outline for selected square
                    Polygon outline = createHex(cx, cy);
                    outline.setFill(Color.TRANSPARENT);
                    outline.setStroke(Color.GOLD);
                    outline.setStrokeWidth(4.0);
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
                        highlight.setStrokeWidth(2.0);
                        highlight.setStrokeType(StrokeType.INSIDE);

                    }
                    highlightGroup.getChildren().add(highlight);
                }
            }

            minX = Math.min(minX, cx);
            maxX = Math.max(maxX, cx);
            minY = Math.min(minY, cy);
            maxY = Math.max(maxY, cy);
        }

        double boardWidth = maxX - minX + TILE_RADIUS * 2;
        double boardHeight = maxY - minY + TILE_RADIUS * 2;

        boardGroup.layoutXProperty().bind(root.widthProperty().subtract(boardWidth).divide(2).subtract(minX));
        boardGroup.layoutYProperty().bind(root.heightProperty().subtract(boardHeight).divide(2).subtract(minY));
        highlightGroup.layoutXProperty().bind(boardGroup.layoutXProperty());
        highlightGroup.layoutYProperty().bind(boardGroup.layoutYProperty());
    }

    private void drawUnits() {
        unitGroup.getChildren().clear();

        for (HexPos pos : game.getUnitPositions()) {
            Node unitNode = drawUnitNode(pos);
            unitGroup.getChildren().add(unitNode);
        }

        unitGroup.layoutXProperty().bind(boardGroup.layoutXProperty());
        unitGroup.layoutYProperty().bind(boardGroup.layoutYProperty());
    }

    private Node drawUnitNode(HexPos pos) {
        double cx = hexToPixelX(pos);
        double cy = hexToPixelY(pos);

        Group group = new Group();
        Unit unit = game.getUnitAt(pos);

        // Unit body
        Circle body = new Circle(cx, cy, TILE_RADIUS * 0.35);
        body.setFill(colorFor(unit));
        body.setStroke(Color.BLACK);

        // Health bar sizes
        double barWidth = TILE_RADIUS * 0.6;
        double barHeight = 6;

        double barX = cx - barWidth / 2;
        double barY = cy + TILE_RADIUS * 0.45;

        // Background bar
        Rectangle bg = new Rectangle(barX, barY, barWidth, barHeight);
        bg.setFill(Color.DARKRED);

        // Foreground bar (HP)
        double hpRatio = (double) unit.getHealth() / unit.getType().maxHp;
        Rectangle fg = new Rectangle(barX, barY, barWidth * hpRatio, barHeight);
        fg.setFill(Color.LIMEGREEN);

        group.getChildren().addAll(body, bg, fg);
        return group;
    }

    private void redraw() {
        drawBoard();
        drawUnits();
    }

    private Color colorFor(Unit unit) {
        return switch (unit.getOwner()) {
            case USER ->
                Color.BLUE;
            case AI ->
                Color.RED;
        };
    }

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

    // ----- PIXEL MATH AND CONVERSIONS ----
    private double hexToPixelX(HexPos p) {
        return TILE_RADIUS * (SQRT3 * p.q() + SQRT3 / 2 * p.r());
    }

    private double hexToPixelY(HexPos p) {
        return TILE_RADIUS * (3.0 / 2 * p.r());
    }

    private HexPos pixelToHex(double x, double y) {
        double q = (Math.sqrt(3) / 3 * x - 1.0 / 3 * y) / TILE_RADIUS;
        double r = (2.0 / 3 * y) / TILE_RADIUS;
        return hexRound(q, r);
    }

    private HexPos hexRound(double q, double r) {
        double s = -q - r;

        // Round each axis to nearest int
        int rq = (int) Math.round(q);
        int rr = (int) Math.round(r);
        int rs = (int) Math.round(s);

        // Find diff between rounded value and actual value
        double dq = Math.abs(rq - q);
        double dr = Math.abs(rr - r);
        double ds = Math.abs(rs - s);

        if (dq > dr && dq > ds) {
            rq = -rr - rs;
        } else if (dr > ds) {
            rr = -rq - rs;
        }

        return new HexPos(rq, rr);
    }
}
