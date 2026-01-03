package tbs_game.game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import tbs_game.board.Board;
import tbs_game.board.Tile;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public final class Movement {

    public void move(Game game, HexPos from, HexPos to) {
        Unit mover = game.getUnitAt(from);
        int dist = from.distanceTo(to);

        mover.spendMovementPoints(dist);

        game.moveUnitInternal(from, to);
    }

    public Set<HexPos> getReachableHexes(Game game, HexPos from) {
        Set<HexPos> reachableHexes = new HashSet<>();

        // Confirm a unit is at the tile
        Unit unit = game.getUnitAt(from);
        if (unit == null) {
            return reachableHexes;
        }

        int maxMove = unit.getMovementPoints();
        Board board = game.getBoard();

        Map<HexPos, Integer> costSoFar = new HashMap<>();
        PriorityQueue<HexPos> frontier
                = new PriorityQueue<>(Comparator.comparingInt(costSoFar::get));

        costSoFar.put(from, 0);
        frontier.add(from);

        while (!frontier.isEmpty()) {
            HexPos current = frontier.poll();
            int currentCost = costSoFar.get(current);

            for (HexPos neighbor : board.getNeighbors(current)) {
                if (game.isFriendly(neighbor, unit.getOwner())) {
                    continue;
                }
                Tile tile = board.getTile(neighbor);
                if (!tile.getTerrain().passable) {
                    continue;
                }

                int terrainCost = board.getTile(neighbor).getTerrain().moveCost;
                int newCost = currentCost + terrainCost;

                if (newCost > maxMove) {
                    continue;
                }

                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    frontier.add(neighbor);
                    reachableHexes.add(neighbor);
                }
            }
        }

        return reachableHexes;
    }
}
