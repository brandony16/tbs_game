package tbs_game.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javafx.scene.paint.Color;
import tbs_game.board.Board;
import tbs_game.game.game_helpers.ActionExecutor;
import tbs_game.game.game_helpers.MovementPlanner;
import tbs_game.hexes.AxialPos;
import tbs_game.player.Player;
import tbs_game.player.PlayerType;
import tbs_game.player.RandomAI;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class Game {

    public static final int SEED = 16;

    private static final int MAX_PLAYERS = 16;
    private static final int MIN_PLAYERS = 2;

    private final Board board;
    private final GameState state;

    private final MovementPlanner planner;
    private final ActionExecutor executor;

    // For queueing AI actions
    private final ActionQueue actionQueue = new ActionQueue();

    private final ArrayList<Player> playerList;
    private final int numPlayers;

    private int numPlayersRemaining;
    private int currentPlayerIdx;

    public Game(int width, int height, int numPlayers) {
        this.board = new Board(width, height);
        this.state = new GameState(board);

        this.planner = new MovementPlanner(state);
        this.executor = new ActionExecutor(state);

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
        return this.board;
    }

    public GameState copyState() {
        return state.createSimluationCopy();
    }

    public Player getCurrentPlayer() {
        return state.getCurrentPlayer();
    }

    public List<AxialPos> getPositionsForPlayer(Player player) {
        return state.getUnitPositionsForPlayer(player);
    }

    public int distanceBetween(AxialPos a, AxialPos b) {
        return state.distanceBetween(a, b);
    }

    public AxialPos wrap(AxialPos pos) {
        return state.wrap(pos);
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

    public Collection<AxialPos> getUnitPositions() {
        return state.getAllUnitPositions();
    }

    public ActionQueue getActionQueue() {
        return this.actionQueue;
    }

    public ActionPath getLastExecuted() {
        return executor.getLastExecuted();
    }

    public Unit getUnitAt(AxialPos pos) {
        pos = state.wrap(pos);
        return state.getUnitAt(pos);
    }

    public void placeUnitAt(AxialPos pos, Unit unit) {
        pos = state.wrap(pos);

        state.placeUnitAt(pos, unit);
    }

    public void removeUnitAt(AxialPos pos) {
        pos = state.wrap(pos);

        state.removeUnitAt(pos);
    }

    public void captureUnit(AxialPos attackerPos, AxialPos defenderPos) {
        attackerPos = state.wrap(attackerPos);
        defenderPos = state.wrap(defenderPos);

        state.captureUnit(attackerPos, defenderPos);
    }

    public void moveUnitInternal(AxialPos from, AxialPos to) {
        from = state.wrap(from);
        to = state.wrap(to);

        state.moveUnitInternal(from, to);
    }

    public boolean isValidMove(AxialPos from, AxialPos to) {
        from = state.wrap(from);
        to = state.wrap(to);

        return Rules.isValidMove(state, from, to);
    }

    public boolean isValidAttack(AxialPos attackFrom, AxialPos attackTo) {
        attackFrom = state.wrap(attackFrom);
        attackTo = state.wrap(attackTo);

        return Rules.isValidAttack(state, attackFrom, attackTo);
    }

    public boolean resolveAction(AxialPos from, AxialPos to) {
        from = state.wrap(from);
        to = state.wrap(to);

        Unit unit = state.getUnitAt(from);
        if (unit == null || !unit.getOwner().equals(state.getCurrentPlayer())) {
            return false;
        }

        ActionPath planned = planner.planAction(from, to);
        if (planned == null) {
            return false;
        }
        if (planned.cost > unit.getMovementPoints()) {
            return false;
        }

        if (Rules.isValidMove(state, from, to)) {
            executor.move(planned);
            return true;
        }
        if (Rules.isValidAttack(state, from, to)) {
            executor.attack(from, to);
            return true;
        }

        // Check if unit can move to the penultimate tile then attack
        boolean canAttack = Rules.isValidAttack(state, planned.path.get(planned.path.size() - 1), to);
        boolean canMove = Rules.isValidMove(state, from, planned.path.get(planned.path.size() - 1));
        if (!canMove && !canAttack) {
            return false;
        }

        executor.moveThenAttack(planned);
        return true;
    }

    public boolean moveUnit(AxialPos from, AxialPos to) {
        from = state.wrap(from);
        to = state.wrap(to);

        Unit unit = state.getUnitAt(from);
        if (unit == null || !unit.getOwner().equals(state.getCurrentPlayer())) {
            return false;
        }

        ActionPath planned = planner.planAction(from, to);
        if (planned == null) {
            return false;
        }
        if (planned.cost > unit.getMovementPoints()) {
            return false;
        }
        if (!Rules.isValidMove(state, from, to)) {
            return false;
        }

        executor.move(planned);
        return true;
    }

    public boolean attackUnit(AxialPos from, AxialPos to) {
        from = state.wrap(from);
        to = state.wrap(to);

        Unit unit = state.getUnitAt(from);
        if (unit == null || !unit.getOwner().equals(state.getCurrentPlayer())) {
            return false;
        }

        ActionPath planned = planner.planAction(from, to);
        if (planned == null) {
            return false;
        }
        if (planned.cost > unit.getMovementPoints()) {
            return false;
        }
        if (!Rules.isValidAttack(state, from, to)) {
            return false;
        }
        executor.attack(from, to);
        return true;
    }

    public Set<AxialPos> getReachableHexes(AxialPos from) {
        from = state.wrap(from);

        return planner.getReachableHexes(from);
    }

    public boolean isFriendly(AxialPos pos, Player player) {
        pos = state.wrap(pos);

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
        ArrayList<AxialPos> spawnLocations = SetupHandler.generateSpawnSpots(this, SEED);
        ArrayList<AxialPos> warriorSpawns = SetupHandler.generateUnitSpawns(this, spawnLocations, SEED);
        for (int i = 0; i < numPlayers; i++) {
            Unit settler = new Unit(UnitType.SETTLER, getPlayer(i));
            Unit warrior = new Unit(UnitType.WARRIOR, getPlayer(i));
            state.placeUnitAt(spawnLocations.get(i), settler);
            state.placeUnitAt(warriorSpawns.get(i), warrior);
        }
    }

    public static Game allPlains(int width, int height, int numPlayers) {
        Game game = new Game(width, height, numPlayers);
        game.getBoard().makeAllPlains();

        return game;
    }

    public static Game battleSim(int width, int height, int numPlayers) {
        Game game = new Game(width, height, numPlayers);

        ArrayList<AxialPos> warriorSpawns = SetupHandler.generateSpawnSpots(game, SEED);
        ArrayList<AxialPos> archerSpawns = SetupHandler.generateUnitSpawns(game, warriorSpawns, SEED);

        for (int i = 0; i < game.getNumPlayers(); i++) {
            Unit warrior = new Unit(UnitType.WARRIOR, game.getPlayer(i));
            Unit archer = new Unit(UnitType.ARCHER, game.getPlayer(i));

            game.placeUnitAt(warriorSpawns.get(i), warrior);
            game.placeUnitAt(archerSpawns.get(i), archer);
        }

        // Do other cavalry after archers are placed so no overlap of spawns is possible
        ArrayList<AxialPos> cavalrySpawns = SetupHandler.generateUnitSpawns(game, warriorSpawns, SEED);
        for (int i = 0; i < game.getNumPlayers(); i++) {
            Unit cavalry = new Unit(UnitType.CAVALRY, game.getPlayer(i));
            game.placeUnitAt(cavalrySpawns.get(i), cavalry);
        }

        return game;
    }
}
