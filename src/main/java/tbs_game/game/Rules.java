package tbs_game.game;

import tbs_game.hexes.AxialPos;
import tbs_game.units.AttackType;
import tbs_game.units.Unit;

public class Rules {

    public static boolean canMove(GameState state, AxialPos from, AxialPos to) {
        if (state.isGameOver()) {
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
            return false; // Moving to tile occupied by an unit
        }
        if (unit.hasAttacked() || unit.getMovementPoints() == 0) {
            return false;
        }

        // Determine if there is a path
        Move move = Movement.planMove(state, from, to);
        if (move == null) {
            return false;
        }

        return move.cost <= unit.getMovementPoints();
    }

    public static boolean canAttack(GameState state, AxialPos attackFrom, AxialPos attackTo) {
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
        if (unit.getAttackType() == AttackType.MELEE) {
            return unit.getMovementPoints() >= state.getBoard().getTile(attackTo).cost();
        }

        // Ranged attack
        int range = unit.getType().attackRange;
        int dist = attackFrom.distanceTo(attackTo);
        return dist <= range;
    }

    public static boolean canUnitMoveDistance(Unit unit, AxialPos from, AxialPos to) {
        int maxMoveDist = unit.getMovementPoints();
        int moveDist = from.distanceTo(to);

        return moveDist <= maxMoveDist;
    }

    public static boolean canDoAction(GameState state, AxialPos from, AxialPos to) {
        if (state.isGameOver()) {
            return false;
        }

        Unit unit = state.getUnitAt(from);
        Unit other = state.getUnitAt(to);

        if (unit == null) {
            return false;
        }
        if (other == null) {
            return canMove(state, from, to);
        }

        // Not correct turn or moving to friendly unit
        if (!unit.getOwner().equals(state.getCurrentPlayer()) || unit.getOwner().equals(other.getOwner())) {
            return false;
        }

        int dist = from.distanceTo(to);
        if (dist <= unit.getType().attackRange) {
            return canAttack(state, from, to);
        }

        if (unit.getAttackType() == AttackType.RANGED) {
            return false; // No moving then attacking in one move for ranged units
        }
        if (unit.getAttackType() == AttackType.NONE) {
            return false;
        }

        // Melee units have to be able to move to end pos to attack that pos
        Move move = Movement.planMove(state, from, to);
        int moveRange = unit.getMovementPoints();

        return move.cost <= moveRange;
    }

}
