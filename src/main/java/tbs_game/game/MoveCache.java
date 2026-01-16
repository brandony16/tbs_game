package tbs_game.game;

import tbs_game.hexes.AxialPos;

public class MoveCache {

    private ActionPath move;

    public void store(ActionPath move) {
        this.move = move;
    }

    public void clear() {
        this.move = null;
    }

    public ActionPath get(AxialPos from, AxialPos to) {
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
