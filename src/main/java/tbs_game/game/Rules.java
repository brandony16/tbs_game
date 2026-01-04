package tbs_game.game;

import tbs_game.hexes.HexPos;
import tbs_game.units.AttackType;
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

        // Determine if there is a path
        Move move = Movement.planMove(game, from, to);
        if (move == null) {
            return false;
        }

        if (move.cost > unit.getMovementPoints()) {
            return false;
        }

        game.getMoveCache().store(move);
        return true;
    }

    public static boolean canAttack(Game game, HexPos attackFrom, HexPos attackTo) {
        Unit unit = game.getUnitAt(attackFrom);
        Unit other = game.getUnitAt(attackTo);
        if (unit == null || other == null) {
            return false;
        }
        if (unit.getAttackType() == AttackType.NONE) {
            return false;
        }
        if (!unit.getOwner().equals(game.getCurrentPlayer())) {
            return false; // Not this units turn
        }
        if (unit.getOwner().equals(other.getOwner())) {
            return false;
        }
        if (unit.hasAttacked()) {
            return false;
        }
        if (unit.getAttackType() == AttackType.MELEE) {
            return unit.getMovementPoints() >= game.getBoard().getTile(attackTo).moveCost();
        }

        // Ranged attack
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

        // Not correct turn or moving to friendly unit
        if (!unit.getOwner().equals(game.getCurrentPlayer()) || unit.getOwner().equals(other.getOwner())) {
            return false;
        }

        int dist = from.distanceTo(to);
        if (dist <= unit.getType().attackRange) {
            return canAttack(game, from, to);
        }

        if (unit.getAttackType() == AttackType.RANGED) {
            return false; // No moving then attacking in one move for ranged units
        }
        if (unit.getAttackType() == AttackType.NONE) {
            return false;
        }

        // Melee units have to be able to move to end pos to attack that pos
        Move move = Movement.planMove(game, from, to);
        int moveRange = unit.getMovementPoints();
        if (move.cost > moveRange) {
            return false;
        }

        game.getMoveCache().store(move);
        return true;
    }

}
