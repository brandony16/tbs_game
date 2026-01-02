package game_tests;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tbs_game.game.Combat;
import tbs_game.game.Game;
import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class CombatTest {

    private Game game;
    private Combat combat;
    private HexPos attackerPos;
    private HexPos defenderPos;

    @BeforeEach
    void init() {
        game = new Game(10, 10);
        combat = new Combat();

        attackerPos = new HexPos(0, 0);
        defenderPos = new HexPos(0, 1);
    }

    @AfterEach
    void reset() {
        game = null;
        combat = null;
        attackerPos = null;
        defenderPos = null;
    }

    private Unit setUpBattle() {
        Unit attacker = new Unit(UnitType.SOLDIER, game.getPlayer(1));
        Unit defender = new Unit(UnitType.SOLDIER, game.getPlayer(2));

        game.placeUnitAt(attackerPos, attacker);
        game.placeUnitAt(defenderPos, defender);

        return defender;
    }

    // ----- attack -----
    @Test
    void testAttackDealsDamage() {
        Unit defender = setUpBattle();
        int startHP = defender.getHealth();

        combat.attack(game, attackerPos, defenderPos);

        assertTrue(defender.getHealth() < startHP);
    }

    @Test
    void testAttackDealsExactDamage() {
        Unit defender = setUpBattle();
        Unit attacker = game.getUnitAt(attackerPos);

        int expectedDamage = attacker.getType().attackDamage;
        int startHP = defender.getHealth();

        combat.attack(game, attackerPos, defenderPos);

        assertEquals(startHP - expectedDamage, defender.getHealth());
    }

    @Test
    void testAttackMarksAttackerAsUsed() {
        setUpBattle();
        Unit attacker = game.getUnitAt(attackerPos);

        combat.attack(game, attackerPos, defenderPos);

        assertTrue(attacker.hasAttacked());
    }

    @Test
    void testAttackDoesNotKillSurvivor() {
        setUpBattle();
        combat.attack(game, attackerPos, defenderPos);

        Unit defender = game.getUnitAt(defenderPos);
        assertNotNull(defender);
        assertEquals(game.getPlayer(2), defender.getOwner());
    }

    @Test
    void testAttackKillsAndCapturesUnit() {
        Unit defender = setUpBattle();
        defender.dealDamage(defender.getHealth()); // ensure death

        combat.attack(game, attackerPos, defenderPos);

        assertNull(game.getUnitAt(attackerPos));
        assertNotNull(game.getUnitAt(defenderPos));
        assertEquals(game.getPlayer(1), game.getUnitAt(defenderPos).getOwner());
    }
}
