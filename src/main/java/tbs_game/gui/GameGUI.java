package tbs_game.gui;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tbs_game.board.Board;
import tbs_game.game.Game;

public class GameGUI {

    private static final int TILE_SIZE = 75;

    private final Pane root;
    private final Game game;
    private final Group boardGroup;

    public GameGUI(Game game) {
        this.game = game;
        this.root = new Pane();
        this.boardGroup = new Group();
        root.getChildren().add(boardGroup);

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
    }
}
