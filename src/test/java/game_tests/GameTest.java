package game_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Game;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class GameTest {

    private Game game;
    private final AxialPos start = new OffsetPos(5, 5).toAxial();

    @BeforeEach
    void init() {
        game = Game.allPlains(10, 10, 2);
    }

    @Test
    void placeUnit() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());

        assertNull(game.getUnitAt(start));
        game.placeUnitAt(start, unit);
        assertNotNull(game.getUnitAt(start));
        assertEquals(unit, game.getUnitAt(start));
    }

    @Test
    void moveCorrectlyUpdatesState() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        game.placeUnitAt(start, unit);

        AxialPos to = start.neighbor(2);
        assertTrue(game.canMove(start, to));
        assertTrue(game.moveUnit(start, to));

        assertNull(game.getUnitAt(start));
        assertEquals(unit, game.getUnitAt(to));
    }

    @Test
    void unitCannotMoveOnOtherPlayersTurn() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(start, unit);

        AxialPos to = start.neighbor(2);

        assertFalse(game.canMove(to, start));
        assertFalse(game.moveUnit(start, to));
    }

    @Test
    void unitCannotMoveWithoutMovementPoints() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        game.placeUnitAt(start, unit);

        AxialPos to = start.neighbor(2);
        unit.spendMovementPoints(unit.getMovementPoints());

        assertFalse(game.canMove(start, to));
        assertFalse(game.moveUnit(start, to));
    }

    @Test
    void unitCannotAttackTwiceInOneTurn() {
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(start, attacker);

        AxialPos defenderPos = start.neighbor(2);
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(defenderPos, defender);

        assertTrue(game.canAttack(start, defenderPos));
        assertTrue(game.attackUnit(start, defenderPos));

        assertTrue(attacker.hasAttacked());
        assertFalse(game.canAttack(start, defenderPos));
    }

    @Test
    void killingAUnitRemovesItstartBoard() {
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(0));
        game.placeUnitAt(start, attacker);

        AxialPos defenderPos = start.neighbor(2);
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(defenderPos, defender);

        defender.dealDamage(defender.getHealth());
        assertTrue(defender.isDead());

        assertTrue(game.attackUnit(start, defenderPos));

        assertNull(game.getUnitAt(start));
        assertNotNull(game.getUnitAt(defenderPos));
        assertEquals(attacker, game.getUnitAt(defenderPos));
    }

    @Test
    void endingTurnResetsUnits() {
        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        game.placeUnitAt(start, unit);

        unit.spendMovementPoints(unit.getMovementPoints());
        unit.markAttacked();
        assertFalse(unit.canAct());

        game.endTurn();
        game.endTurn(); // Back to original player

        assertTrue(unit.canAct());
    }
}
