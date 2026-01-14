package tbs_game.game;

import tbs_game.hexes.AxialPos;

public class MoveCache {

    private Move move;

    public void store(Move move) {
        this.move = move;
    }

    public void clear() {
        this.move = null;
    }

    public Move get(AxialPos from, AxialPos to) {
        if (move == null || move.path == null) {
            return null;
        }
        if (!move.from.equals(from)) {
            return null;
        }
        if (!move.to.equals(to)) {
            return null;
        }

        return move;
    }
}
