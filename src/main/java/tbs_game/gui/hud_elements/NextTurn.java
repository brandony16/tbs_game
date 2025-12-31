package tbs_game.gui.hud_elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import tbs_game.game.Game;
import tbs_game.gui.HudView;

public class NextTurn {

    private StackPane nextTurn;
    private Button nextTurnButton;
    private Label title;
    private ImageView icon;

    public NextTurn() {
        this.nextTurn = PanelFactory.createHudPanel(200, 200, Color.TRANSPARENT);
        this.nextTurnButton = new Button();
        initNextTurn();
    }

    public StackPane getRoot() {
        return this.nextTurn;
    }

    public Button getButton() {
        return this.nextTurnButton;
    }

    public void updateTurnText(Game game) {
        if (game.canEndTurn()) {
            icon.setImage(HudIcons.NEXT_TURN);
            recolorIcon(Color.rgb(131, 103, 43));
            title.setText("NEXT TURN");
            nextTurnButton.setDisable(false);
        } else {
            icon.setImage(HudIcons.ACTIONS_REQUIRED);
            recolorIcon(Color.rgb(48, 48, 48));
            title.setText("ACTIONS REQUIRED");
            nextTurnButton.setDisable(true);
        }
    }

    private void initNextTurn() {
        VBox content = new VBox(4);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        icon = buildIcon(HudIcons.ACTIONS_REQUIRED, 96);
        recolorIcon(Color.rgb(48, 48, 48));

        title = new Label("ACTIONS REQUIRED");
        title.setFont(HudView.HUD_FONT);
        title.setWrapText(true);
        title.setTextAlignment(TextAlignment.CENTER);

        title.getStyleClass().add("next-turn-title");

        content.getChildren().addAll(icon, title);

        nextTurnButton.setGraphic(content);
        nextTurnButton.getStyleClass().add("next-turn-button");

        // Button should fill the panel
        nextTurnButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        nextTurn.getChildren().add(nextTurnButton);
    }

    private ImageView buildIcon(Image img, double size) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setMouseTransparent(true);
        return iv;
    }

    private void recolorIcon(Color color) {
        icon.setEffect(null);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1);

        Blend blend = new Blend(
                BlendMode.SRC_ATOP,
                colorAdjust,
                new ColorInput(
                        0, 0,
                        icon.getImage().getWidth(),
                        icon.getImage().getHeight(),
                        color
                )
        );

        icon.setEffect(blend);
    }
}
