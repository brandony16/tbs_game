package tbs_game.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    public Game(int radius) {
        this.board = new Board(radius);
        this.units = new HashMap<>();
        this.player1 = Player.USER;
        this.player2 = Player.AI;

        setUpGame();
    }

    public Board getBoard() {
        return board;
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

    public void moveUnitTo(HexPos from, HexPos to) {
        Unit unit = getUnitAt(from);
        if (unit == null) {
            return;
        }

        if (!validMove(unit, from, to)) {
            return;
        };

        Unit otherUnit = getUnitAt(to);
        if (otherUnit == null) {
        } else if (!otherUnit.getOwner().equals(unit.getOwner())) {
            handleAttack(unit, otherUnit);
        }
    }

    private boolean validMove(Unit unit, HexPos from, HexPos to) {
        int maxMoveDist = unit.getType().moveRange;
        int moveDist = from.distanceTo(to);

        return moveDist <= maxMoveDist;
    }

    private void handleAttack(Unit attacker, Unit defender) {

    }

    private void setUpGame() {
        // Line of soldiers
        for (int i = 0; i < 5; i++) {
            Unit unit = new Unit(UnitType.SOLDIER, player1);
            Unit aiUnit = new Unit(UnitType.SOLDIER, player2);
            placeUnitAt(new HexPos(i - 4, 4), unit);
            placeUnitAt(new HexPos(i, -4), aiUnit);
        }
    }
}
