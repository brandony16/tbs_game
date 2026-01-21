package game_tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.game.Rules;
import tbs_game.game.game_helpers.GameState;
import tbs_game.game.game_helpers.MovementPlanner;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class RulesTest {

    private GameState state;

    private Player player1;
    private Player player2;

    private AxialPos unitPos;
    private AxialPos otherPos;

    @BeforeEach
    void init() {
        Game game = Game.allPlains(20, 20, 2);
        this.state = game.copyState();

        this.player1 = game.getPlayer(0);
        this.player2 = game.getPlayer(1);

        this.unitPos = new OffsetPos(10, 10).toAxial();
    }

    @AfterEach
    void reset() {
        this.state = null;
        this.player1 = null;
        this.player2 = null;

        this.unitPos = null;
        this.otherPos = null;
    }

    void clear() {
        reset();
        init();
    }

    void setUpSolo() {
        clear();

        Unit unit = new Unit(UnitType.WARRIOR, player1);
        state.placeUnitAt(unitPos, unit);
    }

    void setUpBattle() {
        clear();

        Unit unit = new Unit(UnitType.WARRIOR, player1);
        state.placeUnitAt(unitPos, unit);

        otherPos = unitPos.neighbor(0);
        Unit aiUnit = new Unit(UnitType.WARRIOR, player2);
        state.placeUnitAt(otherPos, aiUnit);
    }

    void setUpBattleMoveThenAttack() {
        clear();

        Unit unit = new Unit(UnitType.WARRIOR, player1);
        state.placeUnitAt(unitPos, unit);

        otherPos = unitPos.diagonalNeighbor(1); // 2 moves away
        Unit aiUnit = new Unit(UnitType.WARRIOR, player2);
        state.placeUnitAt(otherPos, aiUnit);
    }

    void setUpFriendly() {
        clear();

        Unit unit = new Unit(UnitType.WARRIOR, player1);
        state.placeUnitAt(unitPos, unit);

        otherPos = unitPos.neighbor(3);
        Unit friendlyUnit = new Unit(UnitType.WARRIOR, player1);
        state.placeUnitAt(otherPos, friendlyUnit);
    }

    void canMoveManyTiles(AxialPos from, List<AxialPos> tiles) {
        for (AxialPos pos : tiles) {
            assertTrue(Rules.isValidMove(state, from, pos));
        }
    }

    // ----- canMove -----
    @Test
    void testValidMoveNoMover() {
        setUpSolo();
        // No unit at 0,0
        assertFalse(Rules.isValidMove(state, new AxialPos(0, 0), new AxialPos(1, 0)));
    }

    @Test
    void testValidMoveWrongTurn() {
        setUpBattle();
        // Not that units turn
        assertFalse(Rules.isValidMove(state, otherPos, otherPos.neighbor(0)));
    }

    @Test
    void testValidMoveIntoOther() {
        setUpFriendly();
        assertFalse(Rules.isValidMove(state, unitPos, otherPos));

        setUpBattle();
        assertFalse(Rules.isValidMove(state, unitPos, otherPos));
    }

    @Test
    void testValidMoveNoActionsRemaining() {
        setUpSolo();
        state.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.isValidMove(state, unitPos, unitPos.neighbor(0)));
    }

    @Test
    void testValidMoveTooFar() {
        setUpSolo();

        Unit unit = state.getUnitAt(unitPos);
        unit.spendMovementPoints(unit.getMovementPoints() - 1); // 1 movement pt left

        // isValid move should not take into account movement points. Only if the move could be done, even if in multiple turns
        assertTrue(Rules.isValidMove(state, unitPos, unitPos.diagonalNeighbor(2)));
        assertTrue(Rules.isValidMove(state, unitPos, unitPos.diagonalNeighbor(4)));
    }

    @Test
    void testValidMoveValid() {
        setUpSolo();
        MovementPlanner planner = new MovementPlanner(state);
        Set<AxialPos> reachableSet = planner.getReachableHexes(unitPos);
        List<AxialPos> movable = new ArrayList<>(reachableSet);

        canMoveManyTiles(unitPos, movable);
    }

    // ----- canAttack -----
    @Test
    void testCanAttackNoAttacker() {
        setUpBattle();
        // No unit at 2,2
        AxialPos emptyPos = new AxialPos(2, 2);
        assertFalse(Rules.isValidAttack(state, emptyPos, otherPos));
    }

    @Test
    void testCanAttackNoTarget() {
        setUpSolo();
        // No unit at neighbor 0
        assertFalse(Rules.isValidAttack(state, unitPos, unitPos.neighbor(0)));
    }

    @Test
    void testCanAttackFriendly() {
        setUpFriendly();
        assertFalse(Rules.isValidAttack(state, unitPos, otherPos));
    }

    @Test
    void testCanAttackAlreadyAttacked() {
        setUpBattle();
        state.getUnitAt(unitPos).markAttacked();
        assertFalse(Rules.isValidAttack(state, unitPos, otherPos));
    }

    @Test
    void testCanAttackOutOfRange() {
        setUpBattleMoveThenAttack();

        assertFalse(Rules.isValidAttack(state, unitPos, unitPos));
    }

    @Test
    void testCanAttackValid() {
        setUpBattle();
        assertTrue(Rules.isValidAttack(state, unitPos, otherPos));
    }
}
