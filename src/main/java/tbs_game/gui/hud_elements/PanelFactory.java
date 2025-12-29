package tbs_game.gui.hud_elements;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PanelFactory {

    private static final Color HUD_BG = Color.rgb(200, 160, 105);

    public static StackPane createHudPanel(double width, double height) {
        StackPane panel = new StackPane();

        panel.setPrefSize(width, height);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Rectangle bg = new Rectangle(width, height, HUD_BG);

        panel.getChildren().add(bg);

        return panel;
    }

    public static StackPane createHudPanel(double width, double height, Color color) {
        StackPane panel = new StackPane();

        panel.setPrefSize(width, height);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Rectangle bg = new Rectangle(width, height, color);

        panel.getChildren().add(bg);

        return panel;
    }
}
