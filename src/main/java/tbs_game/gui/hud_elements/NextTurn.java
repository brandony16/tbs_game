package tbs_game.gui.hud_elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tbs_game.gui.HudView;

public class NextTurn {

    private StackPane nextTurn;
    private Button nextTurnButton;

    public NextTurn() {
        this.nextTurn = PanelFactory.createHudPanel(200, 200);
        this.nextTurnButton = new Button();
        initNextTurn();
    }

    public StackPane getRoot() {
        return this.nextTurn;
    }

    public Button getButton() {
        return this.nextTurnButton;
    }

    private void initNextTurn() {
        VBox content = new VBox(4);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        Label title = new Label("NEXT TURN");
        title.setFont(HudView.HEADER_FONT);

        title.getStyleClass().add("next-turn-title");

        content.getChildren().add(title);

        nextTurnButton.setGraphic(content);
        nextTurnButton.getStyleClass().add("next-turn-button");

        // Button should fill the panel
        nextTurnButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        nextTurn.getChildren().add(nextTurnButton);
    }
}
