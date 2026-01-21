package tbs_game.gui.hud.utils;

import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class PanelFactory {

    public static final Color HUD_BG = Color.rgb(200, 160, 105);
    public static final int STROKE_WIDTH = 6;

    public static StackPane createHudPanel(double width, double height) {
        return createHudPanel(width, height, HUD_BG);
    }

    public static StackPane createHudPanel(double width, double height, Color color) {
        StackPane panel = new StackPane();

        panel.setPrefSize(width, height);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Rectangle bg = new Rectangle(width, height, color);
        bg.setArcWidth(12);
        bg.setArcHeight(12);
        bg.setStroke(Color.rgb(120, 90, 60));
        bg.setStrokeWidth(STROKE_WIDTH);
        bg.setStrokeType(StrokeType.INSIDE);

        panel.getChildren().add(bg);
        StackPane.setMargin(panel, new Insets(12));

        return panel;
    }

    public static StackPane createCirclePanel(double radius, Color color) {
        StackPane panel = new StackPane();

        panel.setPrefSize(radius * 2, radius * 2);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Circle bg = new Circle(radius, color);

        panel.getChildren().add(bg);
        StackPane.setMargin(panel, new Insets(12));

        return panel;
    }
}
