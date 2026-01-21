package tbs_game.gui.hud.battle_preview;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class StatRow extends BorderPane {

    private final Text attackerText;
    private final Text defenderText;

    public StatRow(Font font) {
        attackerText = new Text();
        attackerText.setFont(font);

        defenderText = new Text();
        defenderText.setFont(font);

        setLeft(attackerText);
        setRight(defenderText);

        BorderPane.setAlignment(attackerText, Pos.CENTER_LEFT);
        BorderPane.setAlignment(defenderText, Pos.CENTER_RIGHT);
    }

    public void setAttacker(String value) {
        attackerText.setText(value);
    }

    public void setDefender(String value) {
        defenderText.setText(value);
    }
}
