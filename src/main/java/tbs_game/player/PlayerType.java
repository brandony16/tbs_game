package tbs_game.player;

public enum PlayerType {
    AI("AI"),
    USER("User");

    public String name;

    PlayerType(String name) {
        this.name = name;
    }
}
