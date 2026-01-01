package tbs_game.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tbs_game.board.Board;
import tbs_game.hexes.HexPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class Game {

    private final Board board;
    private final Map<HexPos, Unit> units;
    private final Map<Player, Set<Unit>> unitsByPlayer;

    private final Movement movement;
    private final Combat combat;

    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    public Game(int width, int height) {
        this.board = new Board(width, height);
        this.units = new HashMap<>();
        this.unitsByPlayer = new HashMap<>();

        this.movement = new Movement();
        this.combat = new Combat();

        this.player1 = Player.USER;
        this.player2 = Player.AI;
        this.currentPlayer = player1;

        unitsByPlayer.put(player1, new HashSet<>());
        unitsByPlayer.put(player2, new HashSet<>());
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Player getPlayer(int i) {
        if (i == 1) {
            return this.player1;
        }
        if (i == 2) {
            return this.player2;
        }

        return null;
    }

    public Collection<HexPos> getUnitPositions() {
        return units.keySet();
    }

    public Unit getUnitAt(HexPos pos) {
        return this.units.get(pos);
    }

    public void placeUnitAt(HexPos pos, Unit unit) {
        this.units.put(pos, unit);
        this.unitsByPlayer.get(unit.getOwner()).add(unit);
    }

    public void removeUnitAt(HexPos pos) {
        Unit unit = units.remove(pos);
        if (unit != null) {
            unitsByPlayer.get(unit.getOwner()).remove(unit);
        }
    }

    public void captureUnit(HexPos attackerPos, HexPos defenderPos) {
        removeUnitAt(defenderPos);
        moveUnitInternal(attackerPos, defenderPos);
    }

    public void moveUnitInternal(HexPos from, HexPos to) {
        Unit unit = units.remove(from);
        units.put(to, unit);
    }

    public boolean canMove(HexPos from, HexPos to) {
        return Rules.canMove(this, from, to);
    }

    public boolean canAttack(HexPos attackFrom, HexPos attackTo) {
        return Rules.canAttack(this, attackFrom, attackTo);
    }

    public boolean canPerform(ActionType action, HexPos from, HexPos to) {
        return switch (action) {
            case MOVE ->
                canMove(from, to);
            case ATTACK ->
                canAttack(from, to);
        };
    }

    public boolean moveUnit(HexPos from, HexPos to) {
        if (!Rules.canMove(this, from, to)) {
            return false;
        }

        movement.move(this, from, to);
        return true;
    }

    public boolean attackUnit(HexPos from, HexPos to) {
        if (!Rules.canAttack(this, from, to)) {
            return false;
        }

        combat.attack(this, from, to);
        return true;
    }

    public Set<HexPos> getReachableHexes(HexPos from) {
        return movement.getReachableHexes(this, from);
    }

    public boolean canEndTurn() {
        return unitsByPlayer.get(currentPlayer)
                .stream()
                .noneMatch(Unit::canAct);
    }

    public void endTurn() {
        this.currentPlayer = (currentPlayer.equals(player1))
                ? player2
                : player1;
        startTurn(this.currentPlayer);
    }

    public boolean isFriendly(HexPos pos, Player player) {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner().equals(player);
    }

    private void startTurn(Player player) {
        for (Unit u : unitsByPlayer.get(player)) {
            u.resetTurnState();
        }
    }

    public void setUpGame() {
        // Line of soldiers
        for (int i = 0; i < 5; i++) {
            Unit unit = new Unit(UnitType.CAVALRY, player1);
            Unit aiUnit = new Unit(UnitType.CAVALRY, player2);
            placeUnitAt(new HexPos(i - 4, 4), unit);
            placeUnitAt(new HexPos(i, -4), aiUnit);
        }

        Unit unit = new Unit(UnitType.CAVALRY, player1);
        Unit aiUnit = new Unit(UnitType.CAVALRY, player2);
        placeUnitAt(new HexPos(0, 0), unit);
        placeUnitAt(new HexPos(0, 1), aiUnit);
    }
}
