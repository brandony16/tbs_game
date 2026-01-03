package tbs_game.units;

public enum UnitType {
    SOLDIER(2, 1, 4, 10, "Soldier"),
    ARCHER(2, 2, 2, 8, "Archer"),
    CAVALRY(3, 1, 3, 12, "Cavalry"),
    SETTLER(2, 0, 0, 1, "Settler");

    public final int moveRange;
    public final int attackRange;
    public final int attackDamage;
    public final int maxHp;
    public final String name;

    UnitType(int moveRange, int attackRange, int attackDamage, int maxHp, String name) {
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.attackDamage = attackDamage;
        this.maxHp = maxHp;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
