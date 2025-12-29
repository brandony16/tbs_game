package tbs_game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tbs_game.HexPos;
import tbs_game.game.Game;
import tbs_game.gui.hud_elements.PanelFactory;
import tbs_game.gui.hud_elements.UnitInfo;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class HudView {

    private final Game game;
    private final StackPane hudLayer;

    // HUD layer
    private Text turnText;
    private StackPane turnInfo;
    private UnitInfo unitInfo;

    private StackPane battleInfo;
    private Text attackerStats;
    private Text defenderStats;

    public HudView(Game game) {
        this.game = game;
        this.hudLayer = new StackPane();
        this.unitInfo = new UnitInfo();
    }

    public StackPane getHudRoot() {
        return this.hudLayer;
    }

    public void initHUD() {
        initTurnHUD();
        initBattleInfoHUD();

        // Add HUD on top of everything
        hudLayer.getChildren().addAll(turnInfo, unitInfo.getRoot(), battleInfo);

        // Set alignments
        StackPane.setAlignment(turnInfo, Pos.TOP_LEFT);
        StackPane.setAlignment(unitInfo.getRoot(), Pos.BOTTOM_LEFT);
        StackPane.setAlignment(battleInfo, Pos.BOTTOM_CENTER);
    }

    private void initTurnHUD() {
        turnInfo = PanelFactory.createHudPanel(300, 50);

        turnText = new Text();
        turnText.setFont(Font.font(20));

        StackPane.setAlignment(turnText, Pos.CENTER_LEFT);
        StackPane.setMargin(turnText, new Insets(10));

        turnInfo.getChildren().add(turnText);
    }

    private void initBattleInfoHUD() {
        battleInfo = PanelFactory.createHudPanel(360, 200);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);

        attackerStats = new Text();
        attackerStats.setFont(Font.font(16));

        defenderStats = new Text();
        defenderStats.setFont(Font.font(16));

        statsRow.getChildren().addAll(attackerStats, defenderStats);
        content.getChildren().add(statsRow);

        battleInfo.getChildren().add(content);

        battleInfo.setVisible(false);
    }

    public void updateHUD(HexPos selected) {
        // Update player turn
        Player current = game.getCurrentPlayer();
        turnText.setText("Player Turn: " + current.name());
        turnText.setFill(current == Player.USER ? Color.BLACK : Color.DARKRED);

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
        battleInfo.setVisible(false);
    }

    public void showCombatPreview(HoverContext context) {
        if (!context.canAttack()) {
            battleInfo.setVisible(false);
            return;
        }

        updateCombatPreview(context);
        battleInfo.setVisible(true);
    }

    private void updateCombatPreview(HoverContext context) {
        Unit attacker = game.getUnitAt(context.selected());
        Unit defender = game.getUnitAt(context.hovered());

        if (attacker == null || defender == null) {
            throw new Error("Combat preview must have attacker and defender units");
        }

        attackerStats.setText("This is the attacker");
        defenderStats.setText("This is the defender");
    }
}
