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
import tbs_game.hexes.AxialPos;
import tbs_game.units.Unit;

public final class Movement {

    private static final int INF = Integer.MAX_VALUE / 4;

    public static Move planMove(GameState state, AxialPos from, AxialPos to) {
        ArrayList<AxialPos> path = findPath(from, to, state);
        if (path == null) {
            return null;
        }

        int cost = countMovementCost(path, state);
        return new Move(from, to, path, cost);
    }

    public static void move(GameState state, AxialPos from, AxialPos to) {
        Move move = planMove(state, from, to);

        Unit mover = state.getUnitAt(from);
        ArrayList<AxialPos> path = move.path;
        AxialPos prev = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            AxialPos step = path.get(i);
            int cost = state.getBoard().getTile(step).cost();

            if (mover.getMovementPoints() < cost) {
                break;
            }

            state.moveUnitInternal(prev, step);
            mover.spendMovementPoints(cost);
            prev = step;
        }
    }

    public static Set<AxialPos> getReachableHexes(GameState state, AxialPos from) {
        Set<AxialPos> reachableHexes = new HashSet<>();

        // Confirm a unit is at the tile
        Unit unit = state.getUnitAt(from);
        if (unit == null) {
            return reachableHexes;
        }

        int maxMove = unit.getMovementPoints();
        Board board = state.getBoard();

        Map<AxialPos, Integer> costSoFar = new HashMap<>();
        PriorityQueue<AxialPos> frontier
                = new PriorityQueue<>(Comparator.comparingInt(costSoFar::get));

        costSoFar.put(from, 0);
        frontier.add(from);

        while (!frontier.isEmpty()) {
            AxialPos current = frontier.poll();
            int currentCost = costSoFar.get(current);

            for (AxialPos neighbor : board.getNeighbors(current)) {
                if (state.isFriendly(neighbor, unit.getOwner())) {
                    continue;
                }
                if (unit.getType().attackRange == 0 && state.getUnitAt(neighbor) != null) {
                    continue; // This unit cannot attack
                }

                Tile tile = board.getTile(neighbor);
                if (!tile.isPassable()) {
                    continue;
                }

                int terrainCost = board.getTile(neighbor).cost();
                int newCost = currentCost + terrainCost;

                if (newCost > maxMove) {
                    continue;
                }

                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    costSoFar.put(neighbor, newCost);
                    reachableHexes.add(neighbor);
                    if (state.getUnitAt(neighbor) == null) { // Only add to frontier if not an attack
                        frontier.add(neighbor);
                    }
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
    public static ArrayList<AxialPos> findPath(AxialPos start, AxialPos end, GameState state) {
        if (start.distanceTo(end) == 1) { // Adjacent tiles
            return new ArrayList<AxialPos>() {
                {
                    add(start);
                    add(end);
                }
            };
        }

        Map<AxialPos, AxialPos> cameFrom = new HashMap<>();
        Map<AxialPos, Integer> gScore = new HashMap<>();
        Map<AxialPos, Integer> fScore = new HashMap<>();

        PriorityQueue<AxialPos> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> fScore.get(n)));

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));
        openSet.add(start);

        Board board = state.getBoard();
        while (!openSet.isEmpty()) {
            AxialPos current = openSet.poll();

            if (current.equals(end)) {
                return reconstructPath(cameFrom, current);
            }

            int currentG = gScore.get(current);

            for (AxialPos neighbor : current.getNeighbors()) {
                Tile tile = board.getTile(neighbor);
                if (!board.isOnBoard(neighbor) || !tile.isPassable()) {
                    continue;
                }

                Unit neighborUnit = state.getUnitAt(neighbor);
                if (neighborUnit != null && !neighbor.equals(end)) {
                    continue; // Cannot move through unit (unless at end of path)
                }

                int tentativeG = currentG + tile.cost();

                int bestKnown = gScore.getOrDefault(neighbor, INF);
                if (tentativeG >= bestKnown) {
                    continue;
                }

                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tentativeG);
                fScore.put(neighbor, tentativeG + heuristic(neighbor, end));
                openSet.add(neighbor);
            }
        }

        return null; // unreachable
    }

    public static int countMovementCost(ArrayList<AxialPos> path, GameState state) {
        int count = 0;
        for (int i = 1; i < path.size(); i++) { // Skip first tile
            AxialPos pos = path.get(i);
            int cost = state.getBoard().getTile(pos).cost();
            count += cost;
        }

        return count;
    }

    private static int heuristic(AxialPos a, AxialPos b) {
        return a.distanceTo(b) * 1; // dist * min terrain cost
    }

    private static ArrayList<AxialPos> reconstructPath(
            Map<AxialPos, AxialPos> cameFrom,
            AxialPos current
    ) {
        ArrayList<AxialPos> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }

        Collections.reverse(path);
        return path;
    }

}
