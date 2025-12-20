package tbs_game.gui;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import tbs_game.Position;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.units.Unit;

public class GameGUI {

    private static final int TILE_SIZE = 75;

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
        Board board = game.getBoard();
        int rows = board.getHeight();
        int cols = board.getWidth();

        boardGroup.getChildren().clear(); // Redraw

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Rectangle rect = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                rect.setFill(Color.GREEN);
                rect.setStroke(Color.BLACK);
                boardGroup.getChildren().add(rect);
            }
        }

        double boardWidth = cols * TILE_SIZE;
        double boardHeight = rows * TILE_SIZE;

        // Center the group dynamically
        boardGroup.layoutXProperty().bind(root.widthProperty().subtract(boardWidth).divide(2));
        boardGroup.layoutYProperty().bind(root.heightProperty().subtract(boardHeight).divide(2));
    }

    private void drawUnits() {
        // iterate Game, draw units on top of tiles
        Board board = game.getBoard();
        int rows = board.getHeight();
        int cols = board.getWidth();

        unitGroup.getChildren().clear();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Unit unit = game.getUnitAt(new Position(x, y));
                if (unit == null) {
                    continue;
                }

                Circle unitNode = new Circle(x * TILE_SIZE + TILE_SIZE / 2.0,
                        y * TILE_SIZE + TILE_SIZE / 2.0,
                        TILE_SIZE * 0.4);

                unitNode.setFill(colorFor(unit));
                unitNode.setStroke(Color.BLACK);
                boardGroup.getChildren().add(unitNode);
            }
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
}
