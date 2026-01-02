package tbs_game.player;

import javafx.scene.paint.Color;

public class Player {

    public String symbol;
    public Color color;

    public Player(String symbol, Color color) {
        this.symbol = symbol;
        this.color = color;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Color getColor() {
        return this.color;
    }
}
