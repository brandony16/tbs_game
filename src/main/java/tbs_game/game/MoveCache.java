package tbs_game.game;

import tbs_game.hexes.HexPos;

public class MoveCache {

    private Move move;

    public void store(Move move) {
        this.move = move;
    }

    public void clear() {
        this.move = null;
    }

    public Move get(HexPos from, HexPos to) {
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
