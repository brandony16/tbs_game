package tbs_game;

import tbs_game.board.Board;
import tbs_game.board.Tile;
import tbs_game.game.Game;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class ConsoleMain {

    public static void main(String[] args) {
        Game game = new Game(4, 4);

        setUpBoard(game);
        printBoardUnits(game);
        printBoardTerrain(game);
    }

    private static void setUpBoard(Game game) {
        Player user = Player.USER;
        Unit soldier = new Unit(UnitType.SOLDIER, user);
        Unit archer = new Unit(UnitType.ARCHER, user);
        Unit calvary = new Unit(UnitType.CAVALRY, user);

        game.placeUnitAt(new Position(0, 2), soldier);
        game.placeUnitAt(new Position(0, 1), archer);
        game.placeUnitAt(new Position(0, 0), calvary);
    }

    private static void printBoardUnits(Game game) {
        Board board = game.getBoard();
        int width = board.getWidth();
        int height = board.getHeight();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Unit unit = game.getUnitAt(new Position(i, j));
                if (unit != null) {
                    System.out.printf("|%s|", unit);
                } else {
                    System.out.print("|EMPTY|");
                }
            }
            System.out.print("\n");
        }
    }

    private static void printBoardTerrain(Game game) {
        Board board = game.getBoard();
        int width = board.getWidth();
        int height = board.getHeight();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Tile tile = board.getTile(i, j);
                System.out.printf("|%s|", tile.getTerrain().name);
            }
            System.out.print("\n");
        }
    }
}
