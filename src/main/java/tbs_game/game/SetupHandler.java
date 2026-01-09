package tbs_game.game;

import java.util.ArrayList;
import java.util.Random;

import tbs_game.board.Board;
import tbs_game.board.Terrain;
import tbs_game.board.Tile;
import tbs_game.hexes.HexPos;

public class SetupHandler {

    public static final int MIN_SPAWN_DIST = 4;
    public static final int MIN_EDGE_DIST = 2;
    public static final HexPos center = new HexPos(0, 0);

    public ArrayList<HexPos> generateSpawnSpots(Game game, int seed) {
        Random random = new Random(seed);
        Board board = game.getBoard();

        int numPlayers = game.getNumPlayers();
        ArrayList<HexPos> spawnableSpots = findSpawnableHexes(board);
        ArrayList<HexPos> spawnSpots = new ArrayList<>();

        // Place first player
        int randomIndex = random.nextInt(spawnableSpots.size());
        HexPos spawnPos = spawnableSpots.get(randomIndex);
        spawnSpots.add(spawnPos);

        for (int i = 1; i < numPlayers; i++) {
            HexPos best = null;
            int bestScore = -1;
            for (HexPos pos : spawnableSpots) {
                int minDist = Integer.MAX_VALUE;

                for (HexPos spawn : spawnSpots) {
                    minDist = Math.min(minDist, spawn.distanceTo(pos));
                }

                int score = minDist * 10 + getSpawnScore(pos, board);

                if (score > bestScore) {
                    best = pos;
                    bestScore = score;
                }
            }
            spawnSpots.add(best);
            spawnableSpots = updateSpawnableHexes(spawnableSpots, best);
        }

        return spawnSpots;
    }

    public ArrayList<HexPos> generateWarriorSpawns(Game game, ArrayList<HexPos> settlerLocations, int seed) {
        Random random = new Random(seed);
        ArrayList<HexPos> warriorSpawns = new ArrayList<>();
        for (HexPos spawn : settlerLocations) {
            ArrayList<HexPos> spawnableNeighbors = getSpawnableNeighbors(game, spawn);
            int neighborIdx = random.nextInt(spawnableNeighbors.size());
            warriorSpawns.add(spawnableNeighbors.get(neighborIdx));
        }

        return warriorSpawns;
    }

    public ArrayList<HexPos> findSpawnableHexes(Board board) {
        ArrayList<HexPos> spawnableHexes = new ArrayList<>();

        for (HexPos pos : board.getPositions()) {
            if (isValidSpawn(pos, board)) {
                spawnableHexes.add(pos);
            }
        }

        return spawnableHexes;
    }

    private ArrayList<HexPos> getSpawnableNeighbors(Game game, HexPos pos) {
        ArrayList<HexPos> neighbors = pos.getNeighbors();
        ArrayList<HexPos> locations = new ArrayList<>();
        for (HexPos neighbor : neighbors) {
            if (isValidSpawn(neighbor, game.getBoard())) {
                locations.add(neighbor);
            }
        }

        return locations;
    }

    private ArrayList<HexPos> updateSpawnableHexes(ArrayList<HexPos> prevList, HexPos newSpawnSpot) {
        ArrayList<HexPos> updatedPositions = new ArrayList<>();
        for (HexPos pos : prevList) {
            if (pos.distanceTo(newSpawnSpot) >= MIN_SPAWN_DIST) {
                updatedPositions.add(pos);
            }
        }

        return updatedPositions;
    }

    public boolean isValidSpawn(HexPos pos, Board board) {
        if (!board.isOnBoard(pos)) {
            return false;
        }

        Tile tile = board.getTile(pos);
        if (!tile.getTerrain().passable) {
            return false;
        }
        if (board.getHeight() < 10) {
            return true; // small board - dont restrict vertical spawns
        }

        int distToEdge = board.getHeight() / 2;
        int maxEquatorDist = distToEdge - MIN_EDGE_DIST;
        return Math.abs(pos.r()) <= maxEquatorDist;
    }

    public static int getSpawnScore(HexPos pos, Board board) {
        if (!board.getTile(pos).getTerrain().passable) {
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
