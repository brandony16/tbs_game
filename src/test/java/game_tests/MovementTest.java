package game_tests;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.game.GameState;
import tbs_game.game.Movement;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class MovementTest {

    // All tests are under the assumption that movement cost = hex distance, 
    // which is true on maps with tiles that all have a movement cost of 1.
    // Tests for different terrain are in TerrainTest.
    private Game game;
    private GameState state;
    private AxialPos unitPos;
    private AxialPos rightEdgePos;
    private AxialPos leftEdgePos;

    @BeforeEach
    void init() {
        int width = 20;
        int height = 20;
        game = Game.allPlains(width, height, 2);
        state = game.getState();
        unitPos = new OffsetPos(width / 2, height / 2).toAxial();

        leftEdgePos = new OffsetPos(0, 4).toAxial();
        rightEdgePos = new OffsetPos(width - 1, 4).toAxial();
    }

    @AfterEach
    void reset() {
        game = null;
        state = null;
        unitPos = null;
        rightEdgePos = null;
        leftEdgePos = null;
    }

    private void setUpSoloUnit(AxialPos pos) {
        Unit unit = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        game.placeUnitAt(pos, unit);
    }

    private void setUpFriendlyBlocker(AxialPos pos) {
        Unit friendly = new Unit(UnitType.CAVALRY, game.getPlayer(0));
        game.placeUnitAt(pos, friendly);
    }

    private void setUpEnemyUnit(AxialPos pos) {
        Unit enemy = new Unit(UnitType.CAVALRY, game.getPlayer(1));
        game.placeUnitAt(pos, enemy);
    }

    // ----- move -----
    @Test
    void testMoveUpdatesPosition() {
        setUpSoloUnit(unitPos);
        AxialPos target = unitPos.neighbor(0);

        Movement.move(state, unitPos, target);

        assertNull(game.getUnitAt(unitPos));
        assertNotNull(game.getUnitAt(target));
    }

    @Test
    void testMoveSpendsMovementPoints() {
        setUpSoloUnit(unitPos);
        Unit unit = game.getUnitAt(unitPos);
        int startMP = unit.getMovementPoints();

        AxialPos target = unitPos.neighbor(1);
        int dist = unitPos.distanceTo(target);

        Movement.move(state, unitPos, target);

        assertEquals(startMP - dist, unit.getMovementPoints());
    }

    @Test
    void testMoveExactDistanceSpent() {
        setUpSoloUnit(unitPos);
        Unit unit = game.getUnitAt(unitPos);

        AxialPos target = unitPos.add(new AxialPos(unit.getMaxMovementPoints(), 0));
        int dist = unitPos.distanceTo(target);

        Movement.move(state, unitPos, target);

        assertEquals(unit.getMaxMovementPoints() - dist, unit.getMovementPoints());
    }

    @Test
    void testMoveAcrossHorizontalWrap() {
        setUpSoloUnit(rightEdgePos);

        AxialPos target = rightEdgePos.neighbor(0); // 0 is directly to the left
        target = state.wrap(target);

        Movement.move(state, rightEdgePos, target);

        assertNull(game.getUnitAt(rightEdgePos));
        assertNotNull(game.getUnitAt(target));
    }

    // ----- getReachableHexes -----
    @Test
    void testGetReachableHexesNoUnit() {
        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);
        assertTrue(reachable.isEmpty());
    }

    @Test
    void testGetReachableHexesDoesNotIncludeOrigin() {
        setUpSoloUnit(unitPos);
        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);

        assertFalse(reachable.contains(unitPos));
    }

    @Test
    void testGetReachableHexesWithinRange() {
        setUpSoloUnit(unitPos);
        Unit unit = game.getUnitAt(unitPos);
        int range = unit.getMovementPoints();

        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);

        for (AxialPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) <= range);
        }
    }

    @Test
    void testGetReachableHexesExcludesFriendlyUnits() {
        setUpSoloUnit(unitPos);
        AxialPos friendlyPos = unitPos.neighbor(4);
        setUpFriendlyBlocker(friendlyPos);

        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);

        assertFalse(reachable.contains(friendlyPos));
    }

    @Test
    void testGetReachableHexesIncludesEnemyUnits() {
        setUpSoloUnit(unitPos);
        AxialPos enemyPos = unitPos.neighbor(4);
        setUpEnemyUnit(enemyPos);

        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);

        assertTrue(reachable.contains(enemyPos));
    }

    @Test
    void testGetReachableHexesRespectsMovementPoints() {
        setUpSoloUnit(unitPos);
        Unit unit = game.getUnitAt(unitPos);

        // Spend all but 1 movement
        unit.spendMovementPoints(unit.getMovementPoints() - 1);

        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);

        assertEquals(6, reachable.size());
        for (AxialPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) == 1);
        }
    }

    @Test
    void testGetReachableHexesCantMoveThroughOpponenets() {
        setUpSoloUnit(unitPos);

        // Surround unit
        for (AxialPos pos : unitPos.getNeighbors()) {
            setUpEnemyUnit(pos);
        }

        Set<AxialPos> reachable = Movement.getReachableHexes(state, unitPos);

        // Should be just the neighbors of the tile
        assertEquals(6, reachable.size());
        for (AxialPos pos : reachable) {
            assertTrue(unitPos.distanceTo(pos) == 1);
        }
    }

    @Test
    void testReachableHexesWrapHorizontally() {
        setUpSoloUnit(leftEdgePos);

        Unit unit = state.getUnitAt(leftEdgePos);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // Set to 1 movement point for ease
        Set<AxialPos> reachable = Movement.getReachableHexes(state, leftEdgePos);

        // Wrapped neighbor on the left side
        AxialPos wrappedNeighbor = leftEdgePos.neighbor(3); // Neighbor 3 is directly to left
        wrappedNeighbor = state.wrap(wrappedNeighbor);

        assertTrue(reachable.contains(wrappedNeighbor));
    }

    @Test
    void testAllReachableHexesAreCanonical() {
        setUpSoloUnit(rightEdgePos);

        Set<AxialPos> reachable = Movement.getReachableHexes(game.getState(), rightEdgePos);

        for (AxialPos pos : reachable) {
            assertEquals(pos, state.wrap(pos));
        }
    }

    // ----- findPath ----- ADD MORE TESTS 
    @Test
    void testPathUsesWrappedShortcut() {
        setUpSoloUnit(leftEdgePos);

        AxialPos end = leftEdgePos.add(new AxialPos(-3, 0)); // 3 to left
        end = state.wrap(end);

        ArrayList<AxialPos> path = Movement.findPath(leftEdgePos, end, state);

        assertNotNull(path);
        // Distance + 1 because path includes the starting tile
        assertEquals(state.distanceBetween(leftEdgePos, end) + 1, path.size());
    }
}
