package tbs_game.gui;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
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
    private final Group unitGroup;

    public GameGUI(Game game) {
        this.game = game;
        this.root = new Pane();
        this.boardGroup = new Group();
        this.unitGroup = new Group();
        root.getChildren().addAll(boardGroup, unitGroup);

        drawBoard();
        drawUnits();
    }

    public Pane getRoot() {
        return root;
    }

    private void drawBoard() {
        boardGroup.getChildren().clear();

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

            minX = Math.min(minX, cx);
            maxX = Math.max(maxX, cx);
            minY = Math.min(minY, cy);
            maxY = Math.max(maxY, cy);
        }

        double boardWidth = maxX - minX + TILE_RADIUS * 2;
        double boardHeight = maxY - minY + TILE_RADIUS * 2;

        boardGroup.layoutXProperty().bind(root.widthProperty().subtract(boardWidth).divide(2).subtract(minX));
        boardGroup.layoutYProperty().bind(root.heightProperty().subtract(boardHeight).divide(2).subtract(minY));
    }

    private void drawUnits() {
        unitGroup.getChildren().clear();

        for (HexPos pos : game.getUnitPositions()) {
            double cx = hexToPixelX(pos);
            double cy = hexToPixelY(pos);

            Circle unitNode = new Circle(
                    cx,
                    cy,
                    TILE_RADIUS * 0.5
            );

            Unit unit = game.getUnitAt(pos);
            unitNode.setFill(colorFor(unit));
            unitNode.setStroke(Color.BLACK);

            unitGroup.getChildren().add(unitNode);
        }

        unitGroup.layoutXProperty().bind(boardGroup.layoutXProperty());
        unitGroup.layoutYProperty().bind(boardGroup.layoutYProperty());
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

    private double hexToPixelX(HexPos p) {
        return TILE_RADIUS * (SQRT3 * p.q() + SQRT3 / 2 * p.r());
    }

    private double hexToPixelY(HexPos p) {
        return TILE_RADIUS * (3.0 / 2 * p.r());
    }
}
