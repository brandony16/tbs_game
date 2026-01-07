package tbs_game.gui.hud_elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tbs_game.gui.HudView;
import tbs_game.player.Player;

public class TurnInfo {

    private StackPane turnInfo;
    private Text turnText;

    public TurnInfo() {
        initTurnHUD();
    }

    public StackPane getRoot() {
        return this.turnInfo;
    }

    public void updateInfo(Player current) {
        turnText.setText(current.getType().name + " Turn");
        turnText.setFill(current.isAI() ? Color.DARKRED : Color.BLACK);
    }

    private void initTurnHUD() {
        turnInfo = PanelFactory.createHudPanel(300, 50);

        turnText = new Text();
        turnText.setFont(HudView.HUD_FONT);

        StackPane.setAlignment(turnText, Pos.CENTER);
        StackPane.setMargin(turnText, new Insets(10));

        turnInfo.getChildren().add(turnText);
    }
}
