package tbs_game.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tbs_game.HexPos;
import tbs_game.board.Board;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class Game {

    private final Board board;
    private final Map<HexPos, Unit> units;

    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    public Game(int radius) {
        this.board = new Board(radius);
        this.units = new HashMap<>();
        this.player1 = Player.USER;
        this.player2 = Player.AI;
        this.currentPlayer = player1;

        setUpGame();
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Collection<HexPos> getUnitPositions() {
        return units.keySet();
    }

    public Unit getUnitAt(HexPos pos) {
        return this.units.get(pos);
    }

    public void placeUnitAt(HexPos pos, Unit unit) {
        this.units.put(pos, unit);
    }

    public boolean isValidMove(HexPos from, HexPos to) {
        Unit unit = getUnitAt(from);
        Unit other = getUnitAt(to);
        if (unit == null) {
            return false;
        }
        if (!unit.getOwner().equals(currentPlayer)) {
            return false; // Not this units turn
        }
        if (other != null && other.getOwner().equals(unit.getOwner())) {
            return false; // Moving to tile occupied by friendly unit
        }

        // Distance check
        return validMove(unit, from, to);
    }

    public boolean canAttack(HexPos attackFrom, HexPos attackTo) {
        Unit unit = getUnitAt(attackFrom);
        Unit other = getUnitAt(attackTo);
        if (unit == null || other == null) {
            return false;
        }
        if (unit.getOwner().equals(other.getOwner())) {
            return false;
        }

        int range = unit.getType().attackRange;
        int dist = attackFrom.distanceTo(attackTo);
        return dist <= range;
    }

    public boolean moveUnit(HexPos from, HexPos to) {
        Unit unit = getUnitAt(from);
        if (unit == null) {
            return false;
        }
        if (!unit.getOwner().equals(currentPlayer)) {
            return false; // Not this units turn
        }
        if (!validMove(unit, from, to)) {
            return false;
        }

        Unit otherUnit = getUnitAt(to);
        if (otherUnit == null) {
            handleMove(from, to);
        } else if (!unit.getOwner().equals(otherUnit.getOwner())) {
            handleAttack(unit, otherUnit, from, to);
        }

        endTurn();
        return true;
    }

    public Set<HexPos> getReachableHexes(HexPos from) {
        Set<HexPos> reachableHexes = new HashSet<>();

        // Confirm a unit is at the tile
        Unit unit = units.get(from);
        if (unit == null) {
            return reachableHexes;
        }

        int range = unit.getType().moveRange;

        // See if each hex is in range of the unit
        for (HexPos pos : board.getPositions()) {
            if (from.distanceTo(pos) <= range && !isFriendly(pos, unit.getOwner())) {
                reachableHexes.add(pos);
            }
        }

        return reachableHexes;
    }

    private boolean isFriendly(HexPos pos, Player player) {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner().equals(player);
    }

    private boolean validMove(Unit unit, HexPos from, HexPos to) {
        int maxMoveDist = unit.getType().moveRange;
        int moveDist = from.distanceTo(to);

        return moveDist <= maxMoveDist;
    }

    private void handleAttack(Unit attacker, Unit defender, HexPos attackSq, HexPos defenseSq) {
        int attackDamage = attacker.getType().attackDamage;
        defender.dealDamage(attackDamage);

        if (defender.isDead()) {
            handleMove(attackSq, defenseSq);
        }
    }

    private void handleMove(HexPos from, HexPos to) {
        Unit mover = units.get(from);
        if (mover == null) {
            return;
        }

        // Remove unit from previous square and move it to new square
        units.remove(from);
        units.put(to, mover);
    }

    private void endTurn() {
        currentPlayer = (currentPlayer.equals(player1))
                ? player2
                : player1;
    }

    private void setUpGame() {
        // Line of soldiers
        for (int i = 0; i < 5; i++) {
            Unit unit = new Unit(UnitType.SOLDIER, player1);
            Unit aiUnit = new Unit(UnitType.SOLDIER, player2);
            placeUnitAt(new HexPos(i - 4, 4), unit);
            placeUnitAt(new HexPos(i, -4), aiUnit);
        }

        Unit unit = new Unit(UnitType.SOLDIER, player1);
        Unit aiUnit = new Unit(UnitType.SOLDIER, player2);
        placeUnitAt(new HexPos(0, 0), unit);
        placeUnitAt(new HexPos(0, 1), aiUnit);
    }
}
