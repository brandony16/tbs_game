package tbs_game.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tbs_game.hexes.HexPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class GameState {

    private final Map<HexPos, Unit> units;
    private final Map<Player, Set<Unit>> unitsByPlayer;
    private final Map<Player, Set<HexPos>> positionsByPlayer;

    private Player currentPlayer;

    public GameState() {
        this.units = new HashMap<>();
        this.unitsByPlayer = new HashMap<>();
        this.positionsByPlayer = new HashMap<>();
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

    public Set<HexPos> getUnitPositionsForPlayer(Player p) {
        return positionsByPlayer.get(p);
    }

    public Collection<HexPos> getAllUnitPositions() {
        return units.keySet();
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
}
