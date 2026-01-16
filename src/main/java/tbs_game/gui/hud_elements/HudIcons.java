package tbs_game.gui.hud_elements;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class HudIcons {

    private HudIcons() {
    }

    // Next turn button
    public static final Image ACTIONS_REQUIRED = new Image(HudIcons.class.getResourceAsStream("/ui/actions.png"));
    public static final Image NEXT_TURN = new Image(HudIcons.class.getResourceAsStream("/ui/next_turn.png"));

    // Unit info
    public static final Image MOVE_PTS = new Image(HudIcons.class.getResourceAsStream("/ui/move_pts.png"));
    public static final Image HEALTH = new Image(HudIcons.class.getResourceAsStream("/ui/health.png"));
    public static final Image STRENGTH = new Image(HudIcons.class.getResourceAsStream("/ui/strength.png"));

    public static ImageView buildIcon(Image img, double size) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setMouseTransparent(true);
        return iv;
    }

    public static void recolorIcon(ImageView icon, Color color) {
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
