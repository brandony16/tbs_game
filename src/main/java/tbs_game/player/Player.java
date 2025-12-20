package tbs_game.player;

public enum Player {
  USER("u", "blue"),
  AI("ai", "red");

  public String symbol;
  public String color;

  Player(String symbol, String color) {
    this.symbol = symbol;
  }
}
