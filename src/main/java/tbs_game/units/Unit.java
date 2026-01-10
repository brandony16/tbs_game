package tbs_game.units;

import tbs_game.player.Player;

public class Unit {

    private final UnitType type;
    private final Player owner;
    private int hp;

    private final int maxMovementPoints;
    private int remainingMovementPoints;
    private boolean hasAttacked;

    public Unit(UnitType type, Player owner) {
        this.type = type;
        this.hp = type.maxHp;
        this.owner = owner;

        this.maxMovementPoints = type.moveRange;
        this.remainingMovementPoints = maxMovementPoints;
    }

    public Player getOwner() {
        return this.owner;
    }

    public UnitType getType() {
        return this.type;
    }

    public int getHealth() {
        return this.hp;
    }

    public AttackType getAttackType() {
        return this.type.attackType;
    }

    public int getStrength() {
        return type.attackDamage;
    }

    public int getMaxMovementPoints() {
        return this.maxMovementPoints;
    }

    public int getMovementPoints() {
        return this.remainingMovementPoints;
    }

    public boolean hasActed() {
        return this.remainingMovementPoints != this.maxMovementPoints;
    }

    public boolean hasAttacked() {
        return this.hasAttacked;
    }

    // Unit cannot do anything else after attacking
    public void markAttacked() {
        this.hasAttacked = true;
        this.remainingMovementPoints = 0;
    }

    public void spendMovementPoints(int movementCost) {
        this.remainingMovementPoints = Math.max(0, remainingMovementPoints - movementCost);
    }

    public void resetTurnState() {
        this.remainingMovementPoints = this.maxMovementPoints;
        this.hasAttacked = false;
    }

    public boolean canAct() {
        return this.remainingMovementPoints != 0 && !this.hasAttacked;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public void dealDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public boolean isDead() {
        return this.hp == 0;
    }

    // ----- COPYING -----
    private Unit(UnitType type, Player owner, int hp, int movementPts, boolean hasAttacked) {
        this.type = type;
        this.hp = hp;
        this.owner = owner;

        this.maxMovementPoints = type.moveRange;
        this.remainingMovementPoints = movementPts;
        this.hasAttacked = hasAttacked;
    }

    public Unit createCopy() {
        return new Unit(type, owner, hp, remainingMovementPoints, hasAttacked);
    }
}
