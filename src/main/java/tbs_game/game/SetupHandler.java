package tbs_game.game;

import java.util.ArrayList;
import java.util.Random;

import tbs_game.board.Board;
import tbs_game.hexes.HexPos;

public class SetupHandler {

    public static final int MIN_SPAWN_DIST = 4;

    public ArrayList<HexPos> generateSpawnSpots(Game game, int seed) {
        Random random = new Random(seed);
        Board board = game.getBoard();

        int numPlayers = game.getNumPlayers();
        ArrayList<HexPos> spawnableSpots = findSpawnableHexes(board);
        ArrayList<HexPos> spawnSpots = new ArrayList<>();

        for (int i = 0; i < numPlayers; i++) {
            int randomIndex = random.nextInt(spawnableSpots.size());
            HexPos spawnPos = spawnableSpots.get(randomIndex);
            spawnSpots.add(spawnPos);
            spawnableSpots = updateSpawnableHexes(spawnableSpots, spawnPos);
        }

        return spawnSpots;
    }

    public ArrayList<HexPos> findSpawnableHexes(Board board) {
        ArrayList<HexPos> spawnableHexes = new ArrayList<>();
        for (HexPos pos : board.getPositions()) {
            if (board.isPassable(pos)) {
                spawnableHexes.add(pos);
            }
        }

        return spawnableHexes;
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
}
