package tbs_game.units;

public enum UnitType {
  SOLDIER(1, 1, 4, 10),
  ARCHER(1, 2, 2, 8),
  CAVALRY(2, 1, 3, 12);

  public final int moveRange;
  public final int attackRange;
  public final int attackDamage;
  public final int maxHp;

  UnitType(int moveRange, int attackRange, int attackDamage, int maxHp) {
    this.moveRange = moveRange;
    this.attackRange = attackRange;
    this.attackDamage = attackDamage;
    this.maxHp = maxHp;
  }
}
