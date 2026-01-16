package tbs_game.game;

import tbs_game.board.Tile;
import tbs_game.hexes.AxialPos;
import tbs_game.units.AttackType;
import tbs_game.units.Unit;

public class Rules {

    public static boolean isValidMove(GameState state, AxialPos from, AxialPos to) {
        if (state.isGameOver()) {
            return false;
        }

        Tile tile = state.getBoard().getTile(to);
        if (!tile.isPassable()) {
            return false;
        }

        Unit unit = state.getUnitAt(from);
        Unit other = state.getUnitAt(to);
        if (unit == null) {
            return false;
        }
        if (!unit.getOwner().equals(state.getCurrentPlayer())) {
            return false; // Not this units turn
        }
        if (other != null) {
            return false; // Moving to tile occupied by an unit.
        }

        return !unit.hasAttacked();
    }

    public static boolean isValidAttack(GameState state, AxialPos attackFrom, AxialPos attackTo) {
        if (state.isGameOver()) {
            return false;
        }

        Unit unit = state.getUnitAt(attackFrom);
        Unit other = state.getUnitAt(attackTo);
        if (unit == null || other == null) {
            return false;
        }
        if (unit.getAttackType() == AttackType.NONE) {
            return false;
        }
        if (!unit.getOwner().equals(state.getCurrentPlayer())) {
            return false; // Not this units turn
        }
        if (unit.getOwner().equals(other.getOwner())) {
            return false;
        }
        if (unit.hasAttacked()) {
            return false;
        }

        int range = unit.getType().attackRange;
        int dist = state.distanceBetween(attackFrom, attackTo);
        return dist <= range;
    }
}
