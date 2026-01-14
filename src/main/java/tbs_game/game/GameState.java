package tbs_game.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tbs_game.board.Board;
import tbs_game.hexes.HexPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class GameState {

    private final Board board;
    private final int width;

    private final Map<HexPos, Unit> units;
    private final Map<Player, Set<Unit>> unitsByPlayer;
    private final Map<Player, Set<HexPos>> positionsByPlayer;

    private Player currentPlayer;
    private boolean isGameOver = false;

    public GameState(Board board) {
        this.board = board;
        this.width = board.getWidth();

        this.units = new HashMap<>();
        this.unitsByPlayer = new HashMap<>();
        this.positionsByPlayer = new HashMap<>();
    }

    public HexPos wrap(HexPos pos) {
        // Get offset col
        int parity = pos.r() & 1;
        int col = pos.q() + (pos.r() - parity) / 2;

        col = ((col % width) + width) % width;

        return new HexPos(col, pos.r());
    }

    public void endGame() {
        this.isGameOver = true;
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }

    public boolean isFriendly(HexPos pos, Player player) {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner().equals(player);
    }

    public void captureUnit(HexPos attackerPos, HexPos defenderPos) {
        removeUnitAt(defenderPos);
        moveUnitInternal(attackerPos, defenderPos);
    }

    public Board getBoard() {
        return this.board;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public void addNewPlayer(Player player) {
        unitsByPlayer.put(player, new HashSet<>());
        positionsByPlayer.put(player, new HashSet<>());
    }

    public List<HexPos> getUnitPositionsForPlayer(Player p) {
        return new ArrayList<>(positionsByPlayer.get(p));
    }

    public Collection<HexPos> getAllUnitPositions() {
        return List.copyOf(units.keySet());
    }

    public Unit getUnitAt(HexPos pos) {
        return units.get(pos);
    }

    public void placeUnitAt(HexPos pos, Unit unit) {
        if (getUnitAt(pos) != null) {
            throw new IllegalArgumentException("Cannot place unit on an already occupied tile");
        }

        units.put(pos, unit);
        unitsByPlayer.get(unit.getOwner()).add(unit);
        positionsByPlayer.get(unit.getOwner()).add(pos);
    }

    public void removeUnitAt(HexPos pos) {
        Unit unit = units.remove(pos);
        if (unit != null) {
            unitsByPlayer.get(unit.getOwner()).remove(unit);
            positionsByPlayer.get(unit.getOwner()).remove(pos);
        }
    }

    public void moveUnitInternal(HexPos from, HexPos to) {
        Unit unit = units.remove(from);
        if (unit == null) {
            throw new IllegalArgumentException("No unit exists at pos " + from.toString());
        }

        units.put(to, unit);
        positionsByPlayer.get(unit.getOwner()).remove(from);
        positionsByPlayer.get(unit.getOwner()).add(to);
    }

    public boolean canEndTurn() {
        return unitsByPlayer.get(currentPlayer)
                .stream()
                .allMatch(Unit::hasActed);
    }

    public void startTurn(Player player) {
        for (Unit u : unitsByPlayer.get(player)) {
            u.resetTurnState();
        }
    }

    // ----- COPYING FOR SIMLUATION -----
    private GameState(
            Board board,
            Map<HexPos, Unit> units,
            Map<Player, Set<Unit>> unitsByPlayer,
            Map<Player, Set<HexPos>> positionsByPlayer,
            Player currentPlayer
    ) {
        this.board = board;
        this.width = board.getWidth();

        this.units = units;
        this.unitsByPlayer = unitsByPlayer;
        this.positionsByPlayer = positionsByPlayer;
        this.currentPlayer = currentPlayer;
    }

    public GameState createSimluationCopy() {
        Map<HexPos, Unit> unitsCopy = new HashMap<>();
        Map<Player, Set<Unit>> unitsByPlayerCopy = new HashMap<>();
        Map<Player, Set<HexPos>> positionsByPlayerCopy = new HashMap<>();

        // Initialize player maps
        for (Player p : unitsByPlayer.keySet()) {
            unitsByPlayerCopy.put(p, new HashSet<>());
            positionsByPlayerCopy.put(p, new HashSet<>());
        }

        // Copy units + positions
        for (Map.Entry<HexPos, Unit> entry : units.entrySet()) {
            HexPos pos = entry.getKey();
            Unit original = entry.getValue();

            Unit unitCopy = original.createCopy();

            unitsCopy.put(pos, unitCopy);
            unitsByPlayerCopy.get(unitCopy.getOwner()).add(unitCopy);
            positionsByPlayerCopy.get(unitCopy.getOwner()).add(pos);
        }

        return new GameState(
                board,
                unitsCopy,
                unitsByPlayerCopy,
                positionsByPlayerCopy,
                currentPlayer
        );
    }
}
