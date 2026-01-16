package tbs_game.units;

public enum UnitType {
    WARRIOR(2, AttackType.MELEE, 1, 4, 10, "Warrior", "warrior.png"),
    ARCHER(2, AttackType.RANGED, 2, 2, 8, "Archer", "slinger.png"),
    CAVALRY(3, AttackType.MELEE, 1, 3, 12, "Cavalry", "settler.png"),
    SETTLER(2, AttackType.NONE, 0, 0, 1, "Settler", "settler.png");

    public final int moveRange;
    public final AttackType attackType;
    public final int attackRange;
    public final int attackDamage;
    public final int maxHp;
    public final String name;
    public final String spritePath;

    UnitType(int moveRange, AttackType attackType, int attackRange, int attackDamage, int maxHp, String name, String spritePath) {
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.attackDamage = attackDamage;
        this.maxHp = maxHp;
        this.name = name;
        this.attackType = attackType;
        this.spritePath = "/units/" + spritePath;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
