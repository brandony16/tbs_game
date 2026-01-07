package tbs_game.player;

import javafx.scene.paint.Color;

public class Player {

    public PlayerType type;
    public Color color;
    public AI ai;

    public Player(PlayerType type, Color color, AI ai) {
        this.type = type;
        this.color = color;
        this.ai = ai;
    }

    public PlayerType getType() {
        return this.type;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isAI() {
        return type == PlayerType.AI;
    }

    public AI getAI() {
        return this.ai;
    }
}
