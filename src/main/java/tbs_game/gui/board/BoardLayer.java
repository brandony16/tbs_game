package tbs_game.gui.board;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.gui.HexMath;
import tbs_game.hexes.HexPos;

public class BoardLayer {

    private final Game game;
    private final Group boardRoot = new Group();

    public BoardLayer(Game game) {
        this.game = game;
    }

    public Group getRoot() {
        return this.boardRoot;
    }

    public void drawBoard() {
        boardRoot.getChildren().clear();

        Board board = game.getBoard();

        for (HexPos pos : board.getPositions()) {
            double cx = HexMath.hexToPixelX(pos);
            double cy = HexMath.hexToPixelY(pos);

            Polygon hex = HexFactory.createHex(cx, cy);
            hex.setFill(board.getTile(pos).getTerrain().color);
            hex.setStroke(Color.BLACK);
            boardRoot.getChildren().add(hex);
        }
    }
}
