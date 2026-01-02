package tbs_game.game;

import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public class Rules {

    public static boolean canMove(Game game, HexPos from, HexPos to) {
        Unit unit = game.getUnitAt(from);
        Unit other = game.getUnitAt(to);
        if (unit == null) {
            return false;
        }
        if (!unit.getOwner().equals(game.getCurrentPlayer())) {
            return false; // Not this units turn
        }
        if (other != null) {
            return false; // Moving to tile occupied by an unit
        }
        if (unit.hasAttacked() || unit.getMovementPoints() == 0) {
            return false;
        }

        // Distance check
        return canUnitMoveDistance(unit, from, to);
    }

    public static boolean canAttack(Game game, HexPos attackFrom, HexPos attackTo) {
        Unit unit = game.getUnitAt(attackFrom);
        Unit other = game.getUnitAt(attackTo);
        if (unit == null || other == null) {
            return false;
        }
        if (unit.getOwner().equals(other.getOwner())) {
            return false;
        }
        if (unit.hasAttacked()) {
            return false;
        }

        int range = unit.getType().attackRange;
        int dist = attackFrom.distanceTo(attackTo);
        return dist <= range;
    }

    public static boolean canUnitMoveDistance(Unit unit, HexPos from, HexPos to) {
        int maxMoveDist = unit.getMovementPoints();
        int moveDist = from.distanceTo(to);

        return moveDist <= maxMoveDist;
    }

    public static boolean canDoAction(Game game, HexPos from, HexPos to) {
        Unit unit = game.getUnitAt(from);
        Unit other = game.getUnitAt(to);

        if (unit == null) {
            return false;
        }
        if (other == null) {
            return canMove(game, from, to);
        }

        int dist = from.distanceTo(to);
        if (dist <= unit.getType().attackRange) {
            return canAttack(game, from, to);
        }

        // Now just a multimove attack
        if (!unit.getOwner().equals(game.getCurrentPlayer()) || unit.getOwner().equals(other.getOwner())) {
            return false;
        }

        // If can move x tiles, then can move x - 1 tiles then attack
        return canUnitMoveDistance(unit, from, to);
    }

}
