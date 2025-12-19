package tbs_game.game;

import tbs_game.Position;
import tbs_game.board.Board;
import tbs_game.units.Unit;

public class Game {

    private final Board board;
    private final Unit[] units;

    public Game(int width, int height) {
        this.board = new Board(width, height);
        this.units = new Unit[width * height];
    }

    public Board getBoard() {
        return board;
    }

    public Unit getUnitAt(Position pos) {
        return this.units[pos.toIndex(board.getWidth())];
    }

    public void placeUnitAt(Position pos, Unit unit) {
        this.units[pos.toIndex(board.getWidth())] = unit;
    }
}
