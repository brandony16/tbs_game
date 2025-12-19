package tbs_game;

import tbs_game.board.Board;
import tbs_game.board.Tile;
import tbs_game.game.Game;

public class Main {

    public static void main(String[] args) {
        Game game = new Game(4, 4);
        Board board = game.getBoard();

        printBoard(board);
    }

    private static void printBoard(Board board) {
        int width = board.getWidth();
        int height = board.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Tile tile = board.getTile(i, j);
                System.out.printf("|%s|", tile.getTerrain().name);
            }
            System.out.print("\n");
        }
    }
}
