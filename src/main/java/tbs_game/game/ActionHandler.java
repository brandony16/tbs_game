package tbs_game.game;

import java.util.ArrayList;

import tbs_game.hexes.FractionalHex;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public class ActionHandler {

    public static boolean resolveAction(GameState state, HexPos from, HexPos to) {
        if (!Rules.canDoAction(state, from, to)) {
            return false;
        }

        int dist = from.distanceTo(to);
        Unit defender = state.getUnitAt(to);
        if (defender == null) {
            return moveUnit(state, from, to);
        }
        if (dist == 1) {
            return attackUnit(state, from, to);
        }

        ArrayList<HexPos> path = FractionalHex.hexLinedraw(from, to);
        HexPos penultimatePos = path.get(path.size() - 2);
        if (!moveUnit(state, from, penultimatePos)) {
            return false;
        }
        return attackUnit(state, penultimatePos, to);
    }

    public static boolean moveUnit(GameState state, HexPos from, HexPos to) {
        if (!Rules.canMove(state, from, to)) {
            return false;
        }

        Movement.move(state, from, to);
        return true;
    }

    public static boolean attackUnit(GameState state, HexPos from, HexPos to) {
        if (!Rules.canAttack(state, from, to)) {
            return false;
        }

        Combat.attack(state, from, to);
        return true;
    }
}
