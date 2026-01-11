package tbs_game.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javafx.scene.paint.Color;
import tbs_game.board.Board;
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

    // For queueing AI actions
    private final ActionQueue actionQueue = new ActionQueue();

    private final ArrayList<Player> playerList;
    private final int numPlayers;

    private int numPlayersRemaining;
    private int currentPlayerIdx;

    public Game(int width, int height, int numPlayers) {
        this.setup = new SetupHandler();
        this.board = new Board(width, height);
        this.state = new GameState(board);

        // Set up players
        assert (numPlayers >= MIN_PLAYERS);
        assert (numPlayers <= MAX_PLAYERS);

        this.numPlayers = numPlayers;
        this.numPlayersRemaining = numPlayers;
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

    public boolean isGameOver() {
        return this.state.isGameOver();
    }

    public Board getBoard() {
        return board;
    }

    public GameState getState() {
        return this.state;
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

    public int getNumPlayersLeft() {
        return this.numPlayersRemaining;
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
        state.captureUnit(attackerPos, defenderPos);
    }

    public void moveUnitInternal(HexPos from, HexPos to) {
        state.moveUnitInternal(from, to);
    }

    public boolean canMove(HexPos from, HexPos to) {
        return Rules.canMove(state, from, to);
    }

    public boolean canAttack(HexPos attackFrom, HexPos attackTo) {
        return Rules.canAttack(state, attackFrom, attackTo);
    }

    public boolean canPerform(ActionType action, HexPos from, HexPos to) {
        return switch (action) {
            case MOVE ->
                canMove(from, to);
            case ATTACK ->
                canAttack(from, to);
        };
    }

    // MOVE TO SOMETHING IDK WHAT
    public boolean resolveAction(HexPos from, HexPos to) {
        return ActionHandler.resolveAction(state, from, to);
    }

    public boolean moveUnit(HexPos from, HexPos to) {
        return ActionHandler.moveUnit(state, from, to);
    }

    public boolean attackUnit(HexPos from, HexPos to) {
        return ActionHandler.attackUnit(state, from, to);
    }

    public Set<HexPos> getReachableHexes(HexPos from) {
        return Movement.getReachableHexes(state, from);
    }

    public boolean isFriendly(HexPos pos, Player player) {
        return state.isFriendly(pos, player);
    }

    public boolean canEndTurn() {
        return state.canEndTurn();
    }

    public void endTurn() {
        if (!state.canEndTurn()) {
            return;
        }

        updatePlayers();

        this.currentPlayerIdx = (currentPlayerIdx + 1) % numPlayersRemaining;
        state.setCurrentPlayer(playerList.get(currentPlayerIdx));
        startTurn();
    }

    private void startTurn() {
        Player player = state.getCurrentPlayer();

        state.startTurn(player);

        if (player.isAI()) {
            player.getAI().doTurn(this, player);
        }
    }

    private void updatePlayers() {
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            if (state.getUnitPositionsForPlayer(player).isEmpty()) { // No units left
                if (!player.isAI()) { // User lost all units -> loss
                    state.endGame();
                    return;
                }

                playerList.remove(i);
                numPlayersRemaining--;
                if (i < currentPlayerIdx) {
                    // If we remove a player before the current player, the index should shift down one
                    currentPlayerIdx--;
                }
            }
        }

        if (playerList.size() == 1) {
            // One player left
            state.endGame();
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
