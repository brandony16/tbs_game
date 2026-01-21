package game_tests;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.ActionPath;
import tbs_game.game.Game;
import tbs_game.game.game_helpers.ActionExecutor;
import tbs_game.game.game_helpers.GameState;
import tbs_game.game.game_helpers.MovementPlanner;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class ExecutorTest {

    private GameState state;
    private ActionExecutor executor;

    private Player player1;
    private Player player2;
    private AxialPos p1Pos;
    private AxialPos p2Pos;

    @BeforeEach
    void init() {
        Game game = Game.allPlains(10, 10, 2);

        this.state = game.copyState();
        this.executor = new ActionExecutor(state);

        this.player1 = game.getPlayer(0);
        this.player2 = game.getPlayer(1);

        this.p1Pos = new OffsetPos(5, 5).toAxial();
        this.p2Pos = new OffsetPos(6, 5).toAxial();
    }

    @AfterEach
    void reset() {
        this.state = null;
        this.executor = null;

        this.player1 = null;
        this.player2 = null;

        this.p1Pos = null;
        this.p2Pos = null;
    }

    private Unit setUpBattle() {
        Unit attacker = new Unit(UnitType.WARRIOR, player1);
        Unit defender = new Unit(UnitType.WARRIOR, player2);

        state.placeUnitAt(p1Pos, attacker);
        state.placeUnitAt(p2Pos, defender);

        return defender;
    }

    // ----- attack -----
    @Test
    void testAttackDealsDamage() {
        Unit defender = setUpBattle();
        int startHP = defender.getHealth();

        executor.attack(p1Pos, p2Pos);

        assertTrue(defender.getHealth() < startHP);
    }

    @Test
    void testAttackDealsExactDamage() {
        Unit defender = setUpBattle();
        Unit attacker = state.getUnitAt(p1Pos);

        int expectedDamage = attacker.getType().attackDamage;
        int startHP = defender.getHealth();

        executor.attack(p1Pos, p2Pos);

        assertEquals(startHP - expectedDamage, defender.getHealth());
    }

    @Test
    void testAttackMarksAttackerAsUsed() {
        setUpBattle();
        Unit attacker = state.getUnitAt(p1Pos);

        executor.attack(p1Pos, p2Pos);

        assertTrue(attacker.hasAttacked());
    }

    @Test
    void testAttackDoesNotKillSurvivor() {
        setUpBattle();
        executor.attack(p1Pos, p2Pos);

        Unit defender = state.getUnitAt(p2Pos);
        assertNotNull(defender);
        assertEquals(player2, defender.getOwner());
    }

    @Test
    void testAttackKillsAndCapturesUnit() {
        Unit defender = setUpBattle();
        defender.dealDamage(defender.getHealth()); // ensure death

        executor.attack(p1Pos, p2Pos);

        assertNull(state.getUnitAt(p1Pos));
        assertNotNull(state.getUnitAt(p2Pos));
        assertEquals(player1, state.getUnitAt(p2Pos).getOwner());
    }

    // ----- move -----
    private void setUpSoloUnit(AxialPos pos) {
        Unit unit = new Unit(UnitType.CAVALRY, player1);
        state.placeUnitAt(pos, unit);
    }

    private ActionPath plan(AxialPos from, AxialPos to) {
        MovementPlanner planner = new MovementPlanner(state);
        return planner.planAction(from, to);
    }

    @Test
    void testMoveUpdatesPosition() {
        setUpSoloUnit(p1Pos);
        AxialPos target = p1Pos.neighbor(0);

        executor.move(plan(p1Pos, target));

        assertNull(state.getUnitAt(p1Pos));
        assertNotNull(state.getUnitAt(target));
    }

    @Test
    void testMoveSpendsMovementPoints() {
        setUpSoloUnit(p1Pos);
        Unit unit = state.getUnitAt(p1Pos);
        int startMP = unit.getMovementPoints();

        AxialPos target = p1Pos.neighbor(1);
        int dist = p1Pos.distanceTo(target);

        executor.move(plan(p1Pos, target));

        assertEquals(startMP - dist, unit.getMovementPoints());
    }

    @Test
    void testMoveExactDistanceSpent() {
        setUpSoloUnit(p1Pos);
        Unit unit = state.getUnitAt(p1Pos);

        AxialPos target = p1Pos.add(new AxialPos(unit.getMaxMovementPoints(), 0));
        int dist = p1Pos.distanceTo(target);

        executor.move(plan(p1Pos, target));

        assertEquals(unit.getMaxMovementPoints() - dist, unit.getMovementPoints());
    }

    @Test
    void testMoveAcrossHorizontalWrap() {
        AxialPos rightEdgePos = new OffsetPos(state.getBoard().getWidth() - 1, 4).toAxial();

        setUpSoloUnit(rightEdgePos);

        AxialPos target = rightEdgePos.neighbor(0); // 0 is directly to the left
        target = state.wrap(target);

        executor.move(plan(rightEdgePos, target));

        assertNull(state.getUnitAt(rightEdgePos));
        assertNotNull(state.getUnitAt(target));
    }
}
