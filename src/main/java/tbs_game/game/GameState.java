package tbs_game.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tbs_game.board.Board;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class GameState {

    private final Board board;
    private final int width;

    private final Map<AxialPos, Unit> units;
    private final Map<Player, Set<Unit>> unitsByPlayer;
    private final Map<Player, Set<AxialPos>> positionsByPlayer;

    private Player currentPlayer;
    private boolean isGameOver = false;

    public GameState(Board board) {
        this.board = board;
        this.width = board.getWidth();

        this.units = new HashMap<>();
        this.unitsByPlayer = new HashMap<>();
        this.positionsByPlayer = new HashMap<>();
    }

    public AxialPos wrap(AxialPos pos) {
        // Convert to offset for ease of calculation
        OffsetPos offset = pos.toOffset();

        int newCol = ((offset.col % width) + width) % width;

        // Back to axial
        return new OffsetPos(newCol, pos.r()).toAxial();
    }

    public void endGame() {
        this.isGameOver = true;
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }

    public boolean isFriendly(AxialPos pos, Player player) {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner().equals(player);
    }

    public void captureUnit(AxialPos attackerPos, AxialPos defenderPos) {
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

    public List<AxialPos> getUnitPositionsForPlayer(Player p) {
        return new ArrayList<>(positionsByPlayer.get(p));
    }

    public Collection<AxialPos> getAllUnitPositions() {
        return List.copyOf(units.keySet());
    }

    public Unit getUnitAt(AxialPos pos) {
        return units.get(pos);
    }

    public void placeUnitAt(AxialPos pos, Unit unit) {
        if (getUnitAt(pos) != null) {
            throw new IllegalArgumentException("Cannot place unit on an already occupied tile");
        }

        units.put(pos, unit);
        unitsByPlayer.get(unit.getOwner()).add(unit);
        positionsByPlayer.get(unit.getOwner()).add(pos);
    }

    public void removeUnitAt(AxialPos pos) {
        Unit unit = units.remove(pos);
        if (unit != null) {
            unitsByPlayer.get(unit.getOwner()).remove(unit);
            positionsByPlayer.get(unit.getOwner()).remove(pos);
        }
    }

    public void moveUnitInternal(AxialPos from, AxialPos to) {
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
            Map<AxialPos, Unit> units,
            Map<Player, Set<Unit>> unitsByPlayer,
            Map<Player, Set<AxialPos>> positionsByPlayer,
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
        Map<AxialPos, Unit> unitsCopy = new HashMap<>();
        Map<Player, Set<Unit>> unitsByPlayerCopy = new HashMap<>();
        Map<Player, Set<AxialPos>> positionsByPlayerCopy = new HashMap<>();

        // Initialize player maps
        for (Player p : unitsByPlayer.keySet()) {
            unitsByPlayerCopy.put(p, new HashSet<>());
            positionsByPlayerCopy.put(p, new HashSet<>());
        }

        // Copy units + positions
        for (Map.Entry<AxialPos, Unit> entry : units.entrySet()) {
            AxialPos pos = entry.getKey();
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
