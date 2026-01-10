package tbs_game.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javafx.scene.paint.Color;
import tbs_game.board.Board;
import tbs_game.hexes.FractionalHex;
import tbs_game.hexes.HexPos;
import tbs_game.player.Player;
import tbs_game.player.PlayerType;
import tbs_game.player.RandomAI;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class Game {

    public static final int SEED = 16;

    private static final int MAX_PLAYERS = 16;
    private static final int MIN_PLAYERS = 2;

    private final SetupHandler setup;

    private final Board board;
    private final GameState state;

    private final Movement movement;
    private final MoveCache moveCache = new MoveCache();
    private final ActionQueue actionQueue = new ActionQueue();

    private final Combat combat;

    private final ArrayList<Player> playerList;
    private final int numPlayers;
    private int currentPlayerIdx;

    public Game(int width, int height, int numPlayers) {
        this.setup = new SetupHandler();
        this.board = new Board(width, height);
        this.state = new GameState();

        this.movement = new Movement();
        this.combat = new Combat();

        assert (numPlayers >= MIN_PLAYERS);
        assert (numPlayers <= MAX_PLAYERS);
        this.numPlayers = numPlayers;
        playerList = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new Player(PlayerType.AI, Color.DARKRED, new RandomAI());
            if (i == 0) {
                newPlayer = new Player(PlayerType.USER, Color.BLUE, null);
            }
            playerList.add(newPlayer);
            state.addNewPlayer(newPlayer);
        }
        this.currentPlayerIdx = 0;
        state.setCurrentPlayer(playerList.get(currentPlayerIdx));
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return state.getCurrentPlayer();
    }

    public Set<HexPos> getPositionsForPlayer(Player player) {
        return state.getUnitPositionsForPlayer(player);
    }

    public Player getPlayer(int i) {
        return playerList.get(i);
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public Collection<HexPos> getUnitPositions() {
        return state.getAllUnitPositions();
    }

    public ActionQueue getActionQueue() {
        return this.actionQueue;
    }

    public Unit getUnitAt(HexPos pos) {
        return state.getUnitAt(pos);
    }

    public void placeUnitAt(HexPos pos, Unit unit) {
        state.placeUnitAt(pos, unit);
    }

    public void removeUnitAt(HexPos pos) {
        state.removeUnitAt(pos);
    }

    public void captureUnit(HexPos attackerPos, HexPos defenderPos) {
        removeUnitAt(defenderPos);
        moveUnitInternal(attackerPos, defenderPos);
    }

    public void moveUnitInternal(HexPos from, HexPos to) {
        state.moveUnitInternal(from, to);
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
        return state.canEndTurn();
    }

    public void endTurn() {
        if (!state.canEndTurn()) {
            return;
        }

        this.currentPlayerIdx = (currentPlayerIdx + 1) % numPlayers;
        state.setCurrentPlayer(playerList.get(currentPlayerIdx));
        startTurn();
    }

    public boolean isFriendly(HexPos pos, Player player) {
        Unit unit = state.getUnitAt(pos);
        return unit != null && unit.getOwner().equals(player);
    }

    private void startTurn() {
        Player player = state.getCurrentPlayer();

        state.startTurn(player);

        if (player.isAI()) {
            player.getAI().doTurn(this, player);
        }
    }

    public void setUpGame() {
        ArrayList<HexPos> spawnLocations = setup.generateSpawnSpots(this, SEED);
        ArrayList<HexPos> soldierSpawns = setup.generateWarriorSpawns(this, spawnLocations, SEED);
        for (int i = 0; i < numPlayers; i++) {
            Unit settler = new Unit(UnitType.SETTLER, getPlayer(i));
            Unit soldier = new Unit(UnitType.SOLDIER, getPlayer(i));
            placeUnitAt(spawnLocations.get(i), settler);
            placeUnitAt(soldierSpawns.get(i), soldier);
        }
    }

    public static Game allPlains(int width, int height, int numPlayers) {
        Game game = new Game(width, height, numPlayers);
        game.getBoard().makeAllPlains();

        return game;
    }
}
