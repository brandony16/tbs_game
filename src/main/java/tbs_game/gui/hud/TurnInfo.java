package tbs_game.gui.hud;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import tbs_game.gui.hud.utils.PanelFactory;
import tbs_game.player.Player;

public class TurnInfo {

    private StackPane turnInfo;
    private Text turnText;
    private Rectangle accent;

    public TurnInfo() {
        initTurnHUD();
    }

    public StackPane getRoot() {
        return this.turnInfo;
    }

    public void updateInfo(Player current) {
        turnText.setText(current.isAI() ? "Opponents Moving" : "Your Turn");
        turnText.setFill(current.isAI() ? Color.DARKRED : Color.BLACK);
        accent.setFill(current.isAI() ? Color.DARKRED : Color.DARKGREEN);
    }

    private void initTurnHUD() {
        turnInfo = PanelFactory.createHudPanel(300, 50);

        turnText = new Text();
        turnText.setFont(HudView.HEADER_FONT);

        accent = new Rectangle(12, 50 - 2 * PanelFactory.STROKE_WIDTH);

        HBox content = new HBox(accent, turnText);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);

        StackPane.setMargin(content, new Insets(0, 0, 0, PanelFactory.STROKE_WIDTH));
        turnInfo.getChildren().add(content);
    }
}
