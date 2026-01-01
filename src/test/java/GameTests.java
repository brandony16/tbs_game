
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import tbs_game.game.Game;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class GameTests {

    @Test
    public void placeUnit() {
        Game game = new Game(10, 10);

        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);

        assertNull(game.getUnitAt(from));
        game.placeUnitAt(from, unit);
        assertNotNull(game.getUnitAt(from));
        assertEquals(unit, game.getUnitAt(from));
    }

    @Test
    public void moveCorrectlyUpdatesState() {
        Game game = new Game(10, 10);

        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        HexPos to = new HexPos(1, 0);
        assertTrue(game.canMove(from, to));
        assertTrue(game.moveUnit(from, to));

        assertNull(game.getUnitAt(from));
        assertEquals(unit, game.getUnitAt(to));
    }

    @Test
    public void unitCannotMoveOnOtherPlayersTurn() {
        Game game = new Game(10, 10);

        Unit unit = new Unit(UnitType.SOLDIER, game.getPlayer(2));
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        HexPos to = new HexPos(1, 0);

        assertFalse(game.canMove(to, from));
        assertFalse(game.moveUnit(from, to));
    }

    @Test
    public void unitCannotMoveWithoutMovementPoints() {
        Game game = new Game(10, 10);

        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        HexPos to = new HexPos(1, 0);
        unit.spendMovementPoints(unit.getMovementPoints());

        assertFalse(game.canMove(from, to));
        assertFalse(game.moveUnit(from, to));
    }

    @Test
    public void unitCannotAttackTwiceInOneTurn() {
        Game game = new Game(10, 10);

        HexPos attackerPos = new HexPos(0, 0);
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(attackerPos, attacker);

        HexPos defenderPos = new HexPos(0, 1);
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(2));
        game.placeUnitAt(defenderPos, defender);

        assertTrue(game.canAttack(attackerPos, defenderPos));
        assertTrue(game.attackUnit(attackerPos, defenderPos)); // FAIL

        assertTrue(attacker.hasAttacked());
        assertFalse(game.canAttack(attackerPos, defenderPos));
    }

    @Test
    public void killingAUnitRemovesItFromBoard() {
        Game game = new Game(10, 10);

        HexPos attackerPos = new HexPos(0, 0);
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        game.placeUnitAt(attackerPos, attacker);

        HexPos defenderPos = new HexPos(0, 1);
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(2));
        game.placeUnitAt(defenderPos, defender);

        defender.dealDamage(defender.getHealth());
        assertTrue(defender.isDead());

        assertTrue(game.attackUnit(attackerPos, defenderPos)); // FAIL

        assertNull(game.getUnitAt(attackerPos));
        assertNotNull(game.getUnitAt(defenderPos));
        assertEquals(attacker, game.getUnitAt(defenderPos));
    }

    @Test
    public void endingTurnResetsUnits() {
        Game game = new Game(10, 10);

        Unit unit = new Unit(UnitType.SOLDIER, game.getCurrentPlayer());
        HexPos from = new HexPos(0, 0);
        game.placeUnitAt(from, unit);

        unit.spendMovementPoints(unit.getMovementPoints());
        unit.markAttacked();
        assertFalse(unit.canAct());

        game.endTurn();
        game.endTurn(); // Back to original player

        assertTrue(unit.canAct());
    }
}
