package tbs_game.units;

import tbs_game.player.Player;

public class Unit {

    private UnitType unitType;
    private int hp;
    private Player owner;

    public Unit(UnitType unitType, Player owner) {
        this.unitType = unitType;
        this.hp = unitType.maxHp;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return unitType.toString();
    }
}
