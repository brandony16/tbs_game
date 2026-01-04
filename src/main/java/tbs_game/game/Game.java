package tbs_game.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.paint.Color;
import tbs_game.board.Board;
import tbs_game.hexes.FractionalHex;
import tbs_game.hexes.HexPos;
import tbs_game.player.AI;
import tbs_game.player.Player;
import tbs_game.player.User;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class Game {

    public static final int SEED = 16;

    private static final int MAX_PLAYERS = 10;
    private static final int MIN_PLAYERS = 2;

    private final SetupHandler setup;

    private final Board board;
    private final Map<HexPos, Unit> units;
    private final Map<Player, Set<Unit>> unitsByPlayer;

    private final Movement movement;
    private MoveCache moveCache = new MoveCache();

    private final Combat combat;

    private final ArrayList<Player> playerList;
    private final int numPlayers;
    private int currentPlayerIdx;

    public Game(int width, int height, int numPlayers) {
        this.setup = new SetupHandler();
        this.board = new Board(width, height);
        this.units = new HashMap<>();
        this.unitsByPlayer = new HashMap<>();

        this.movement = new Movement();
        this.combat = new Combat();

        assert (numPlayers >= MIN_PLAYERS);
        assert (numPlayers <= MAX_PLAYERS);
        this.numPlayers = numPlayers;
        playerList = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new AI("i", Color.DARKRED);
            if (i == 0) {
                newPlayer = new User();
            }
            playerList.add(newPlayer);
            unitsByPlayer.put(newPlayer, new HashSet<>());
        }
        this.currentPlayerIdx = 0;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return playerList.get(currentPlayerIdx);
    }

    public Player getPlayer(int i) {
        return playerList.get(i);
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public Collection<HexPos> getUnitPositions() {
        return units.keySet();
    }

    public Unit getUnitAt(HexPos pos) {
        return this.units.get(pos);
    }

    public void placeUnitAt(HexPos pos, Unit unit) {
        if (getUnitAt(pos) != null) {
            return;
        }

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

    public MoveCache getMoveCache() {
        return this.moveCache;
    }

    // MOVE TO SOMETHING IDK WHAT
    public boolean resolveAction(HexPos from, HexPos to) {
        if (!Rules.canDoAction(this, from, to)) {
            return false;
        }

        int dist = from.distanceTo(to);
        Unit defender = getUnitAt(to);
        if (defender == null) {
            return moveUnit(from, to);
        }
        if (dist == 1) {
            return attackUnit(from, to);
        }

        ArrayList<HexPos> path = FractionalHex.hexLinedraw(from, to);
        HexPos penultimatePos = path.get(path.size() - 2);
        if (!moveUnit(from, penultimatePos)) {
            return false;
        }
        return attackUnit(penultimatePos, to);
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
        return unitsByPlayer.get(getCurrentPlayer())
                .stream()
                .noneMatch(Unit::canAct);
    }

    public void endTurn() {
        this.currentPlayerIdx = (currentPlayerIdx + 1) % numPlayers;
        startTurn(this.currentPlayerIdx);
    }

    public boolean isFriendly(HexPos pos, Player player) {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner().equals(player);
    }

    private void startTurn(int playerIdx) {
        Player player = playerList.get(playerIdx);
        for (Unit u : unitsByPlayer.get(player)) {
            u.resetTurnState();
        }
    }

    public void setUpBattleGame() {
        // Line of soldiers
        for (int i = 0; i < 5; i++) {
            Unit unit = new Unit(UnitType.CAVALRY, getPlayer(0));
            Unit aiUnit = new Unit(UnitType.CAVALRY, getPlayer(1));
            placeUnitAt(new HexPos(i - 4, 4), unit);
            placeUnitAt(new HexPos(i, -4), aiUnit);
        }

        Unit unit = new Unit(UnitType.CAVALRY, getPlayer(0));
        Unit aiUnit = new Unit(UnitType.CAVALRY, getPlayer(1));
        placeUnitAt(new HexPos(0, 0), unit);
        placeUnitAt(new HexPos(0, 1), aiUnit);
    }

    public void setUpGame() {
        ArrayList<HexPos> spawnLocations = setup.generateSpawnSpots(this, SEED);
        for (int i = 0; i < numPlayers; i++) {
            Unit settler = new Unit(UnitType.SETTLER, getPlayer(i));
            placeUnitAt(spawnLocations.get(i), settler);
        }
    }
}
