package tbs_game.game;

import java.util.ArrayList;
import java.util.Collections;
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

    private static final int INF = Integer.MAX_VALUE / 4;

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

    /**
     * Finds the shortest path between two HexPos using the A* algorithm
     *
     * @param start - The pos to start at
     * @param end - The pos to end at
     * @return Ordered list of HexPos that represent the path found
     */
    public ArrayList<HexPos> findPath(HexPos start, HexPos end, Game game) {
        Map<HexPos, HexPos> cameFrom = new HashMap<>();
        Map<HexPos, Integer> gScore = new HashMap<>();
        Map<HexPos, Integer> fScore = new HashMap<>();

        PriorityQueue<HexPos> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> fScore.get(n)));

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));
        openSet.add(start);

        Board board = game.getBoard();
        while (!openSet.isEmpty()) {
            HexPos current = openSet.poll();

            if (current.equals(end)) {
                return reconstructPath(cameFrom, current);
            }

            int currentG = gScore.get(current);

            for (HexPos neighbor : current.getNeighbors()) {
                Tile tile = board.getTile(neighbor);
                if (!board.isOnBoard(neighbor) || !tile.isPassable()) {
                    continue;
                }

                int tentativeG = currentG + tile.moveCost();

                int bestKnown = gScore.getOrDefault(neighbor, INF);
                if (tentativeG >= bestKnown) {
                    continue;
                }

                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tentativeG);
                fScore.put(neighbor, tentativeG + heuristic(neighbor, end));

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
            }
        }

        return null; // unreachable
    }

    private static int heuristic(HexPos a, HexPos b) {
        return a.distanceTo(b) * 1; // dist * min terrain cost
    }

    private static ArrayList<HexPos> reconstructPath(
            Map<HexPos, HexPos> cameFrom,
            HexPos current
    ) {
        ArrayList<HexPos> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }

        Collections.reverse(path);
        return path;
    }

}
