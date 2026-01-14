package tbs_game.game;

import java.util.ArrayList;
import java.util.Random;

import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.board.Tile;
import tbs_game.hexes.AxialPos;

public class SetupHandler {

    public static final int MIN_SPAWN_DIST = 4;
    public static final int MIN_EDGE_DIST = 2;

    public static ArrayList<AxialPos> generateSpawnSpots(Game game, int seed) {
        Random random = new Random(seed);
        GameState state = game.getState();
        Board board = game.getBoard();

        int numPlayers = game.getNumPlayers();
        ArrayList<AxialPos> spawnableSpots = findSpawnableHexes(board);
        ArrayList<AxialPos> spawnSpots = new ArrayList<>();

        // Place first player
        int randomIndex = random.nextInt(spawnableSpots.size());
        AxialPos spawnPos = spawnableSpots.get(randomIndex);
        spawnSpots.add(spawnPos);

        for (int i = 1; i < numPlayers; i++) {
            AxialPos best = null;
            int bestScore = -1;
            for (AxialPos pos : spawnableSpots) {
                if (getSpawnableNeighbors(game, pos).isEmpty()) {
                    continue; // No place to put warrior after
                }

                int minDist = Integer.MAX_VALUE;

                for (AxialPos spawn : spawnSpots) {
                    minDist = Math.min(minDist, state.distanceBetween(spawn, pos));
                }

                int score = minDist * 10 + getSpawnScore(pos, board);

                if (score > bestScore) {
                    best = pos;
                    bestScore = score;
                }
            }
            spawnSpots.add(best);
            spawnableSpots = updateSpawnableHexes(state, spawnableSpots, best);
        }

        return spawnSpots;
    }

    public static ArrayList<AxialPos> generateUnitSpawns(Game game, ArrayList<AxialPos> settlerLocations, int seed) {
        Random random = new Random(seed);
        ArrayList<AxialPos> unitSpawns = new ArrayList<>();
        for (AxialPos spawn : settlerLocations) {
            ArrayList<AxialPos> spawnableNeighbors = getSpawnableNeighbors(game, spawn);
            int neighborIdx = random.nextInt(spawnableNeighbors.size());
            unitSpawns.add(spawnableNeighbors.get(neighborIdx));
        }

        return unitSpawns;
    }

    public static ArrayList<AxialPos> findSpawnableHexes(Board board) {
        ArrayList<AxialPos> spawnableHexes = new ArrayList<>();

        for (AxialPos pos : board.getPositions()) {
            if (isValidSpawn(pos, board)) {
                spawnableHexes.add(pos);
            }
        }

        return spawnableHexes;
    }

    private static ArrayList<AxialPos> getSpawnableNeighbors(Game game, AxialPos pos) {
        ArrayList<AxialPos> neighbors = pos.getNeighbors();
        ArrayList<AxialPos> locations = new ArrayList<>();
        for (AxialPos neighbor : neighbors) {
            if (isValidSpawn(neighbor, game.getBoard()) && game.getUnitAt(neighbor) == null) {
                locations.add(neighbor);
            }
        }

        return locations;
    }

    private static ArrayList<AxialPos> updateSpawnableHexes(GameState state, ArrayList<AxialPos> prevList, AxialPos newSpawnSpot) {
        ArrayList<AxialPos> updatedPositions = new ArrayList<>();
        for (AxialPos pos : prevList) {
            if (state.distanceBetween(pos, newSpawnSpot) >= MIN_SPAWN_DIST) {
                updatedPositions.add(pos);
            }
        }

        return updatedPositions;
    }

    public static boolean isValidSpawn(AxialPos pos, Board board) {
        if (!board.isOnBoard(pos)) {
            return false;
        }

        Tile tile = board.getTile(pos);
        if (!tile.isPassable()) {
            return false;
        }
        if (board.getHeight() < 10) {
            return true; // small board - dont restrict vertical spawns
        }

        int distToEdge = board.getHeight() / 2;
        int maxEquatorDist = distToEdge - MIN_EDGE_DIST;
        return Math.abs(pos.r()) <= maxEquatorDist;
    }

    public static int getSpawnScore(AxialPos pos, Board board) {
        if (!board.getTile(pos).isPassable()) {
            return Integer.MIN_VALUE;
        }

        int score = 0;

        int forestNeighbors = board.countNeighbors(pos, Terrain.FOREST);
        int waterNeighbors = board.countNeighbors(pos, Terrain.WATER);
        int mountainNeighbors = board.countNeighbors(pos, Terrain.MOUNTAIN);

        score += forestNeighbors * 10;
        score += waterNeighbors * -5;
        score += mountainNeighbors * -5;

        return score;
    }
}
