package tbs_game.game;

import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public class Combat {

    public void attack(Game game, HexPos attackSq, HexPos defenseSq) {
        Unit attacker = game.getUnitAt(attackSq);
        Unit defender = game.getUnitAt(defenseSq);

        int attackDamage = attacker.getType().attackDamage;
        defender.dealDamage(attackDamage);
        attacker.markAttacked();

        if (defender.isDead()) {
            game.captureUnit(attackSq, defenseSq);
        }
    }
}
