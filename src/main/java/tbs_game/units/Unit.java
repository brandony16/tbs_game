package tbs_game.units;

import tbs_game.player.Player;

public class Unit {

    private UnitType type;
    private int hp;
    private Player owner;

    public Unit(UnitType type, Player owner) {
        this.type = type;
        this.hp = type.maxHp;
        this.owner = owner;
    }

    public Player getOwner() {
        return this.owner;
    }

    public UnitType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
