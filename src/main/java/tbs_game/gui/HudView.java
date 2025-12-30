package tbs_game.gui;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tbs_game.HexPos;
import tbs_game.game.Game;
import tbs_game.gui.hud_elements.BattlePreview;
import tbs_game.gui.hud_elements.TurnInfo;
import tbs_game.gui.hud_elements.UnitInfo;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class HudView {

    public static final String FONT_FAMILY = "System";
    public static final int FONT_SIZE = 16;
    public static final int HEDAER_SIZE = 24;
    public static final Font HUD_FONT = Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE);
    public static final Font HEADER_FONT = Font.font(FONT_FAMILY, FontWeight.BOLD, HEDAER_SIZE);

    private final Game game;
    private final StackPane hudLayer;

    // HUD layer
    private final TurnInfo turnInfo;
    private final UnitInfo unitInfo;
    private final BattlePreview battlePreview;

    public HudView(Game game) {
        this.game = game;
        this.hudLayer = new StackPane();
        this.unitInfo = new UnitInfo();
        this.turnInfo = new TurnInfo();
        this.battlePreview = new BattlePreview();
    }

    public StackPane getHudRoot() {
        return this.hudLayer;
    }

    public void initHUD() {

        // Add HUD on top of everything
        hudLayer.getChildren().addAll(turnInfo.getRoot(), unitInfo.getRoot(), battlePreview.getRoot());

        // Set alignments
        StackPane.setAlignment(turnInfo.getRoot(), Pos.TOP_LEFT);
        StackPane.setAlignment(unitInfo.getRoot(), Pos.BOTTOM_LEFT);
        StackPane.setAlignment(battlePreview.getRoot(), Pos.BOTTOM_CENTER);
    }

    public void updateHUD(HexPos selected) {
        // Update player turn
        Player current = game.getCurrentPlayer();
        turnInfo.updateInfo(current);

        // Update selected unit info
        if (selected != null) {
            Unit unit = game.getUnitAt(selected);
            unitInfo.updateInfo(unit);
            unitInfo.setVisibility(true);
        } else {
            unitInfo.setVisibility(false);
        }
    }

    public void hideCombatPreview() {
        battlePreview.setVisibility(false);
    }

    public void showCombatPreview(HoverContext context) {
        if (!context.canAttack()) {
            battlePreview.setVisibility(false);
            return;
        }

        Unit attacker = game.getUnitAt(context.selected());
        Unit defender = game.getUnitAt(context.hovered());

        battlePreview.updateCombatPreview(attacker, defender);
        battlePreview.setVisibility(true);
    }
}
