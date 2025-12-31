package tbs_game.gui.hud_elements;

import javafx.scene.image.Image;

public class HudIcons {

    private HudIcons() {
    }

    public static final Image ACTIONS_REQUIRED
            = new Image(HudIcons.class.getResourceAsStream("/ui/icon_actions.png"));

    public static final Image NEXT_TURN
            = new Image(HudIcons.class.getResourceAsStream("/ui/icon_next_turn.png"));
}
