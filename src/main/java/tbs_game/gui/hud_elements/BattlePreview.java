package tbs_game.gui.hud_elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tbs_game.gui.HudView;
import tbs_game.units.Unit;

public class BattlePreview {

    private StackPane battleInfo;

    private final StatRow healthRow;
    private final StatRow damageRow;

    public BattlePreview() {
        this.healthRow = new StatRow(HudView.HUD_FONT);
        this.damageRow = new StatRow(HudView.HUD_FONT);

        initBattleInfoHUD();
    }

    public StackPane getRoot() {
        return this.battleInfo;
    }

    public void setVisibility(boolean isVisible) {
        battleInfo.setVisible(isVisible);
    }

    public void updateCombatPreview(Unit attacker, Unit defender) {
        if (attacker == null || defender == null) {
            throw new Error("Combat preview must have attacker and defender units");
        }

        int attackerHealth = attacker.getHealth();
        int defenderHealth = defender.getHealth();

        int attackerDamage = attacker.getStrength();
        int defenderHealthRemaining = Math.max(0, defenderHealth - attackerDamage);

        healthRow.setAttacker("HP: " + attackerHealth);
        healthRow.setDefender("HP: " + defenderHealth + " → " + defenderHealthRemaining);

        int damageTaken = defenderHealth - defenderHealthRemaining;
        damageRow.setAttacker("DMG TAKEN: -");
        damageRow.setDefender("DMG TAKEN: " + damageTaken);
    }

    private void initBattleInfoHUD() {
        battleInfo = PanelFactory.createHudPanel(360, 200);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.TOP_RIGHT);

        BorderPane headers = buildHeaders();

        content.getChildren().addAll(headers, healthRow, damageRow);
        battleInfo.getChildren().add(content);

        battleInfo.setVisible(false);
    }

    private BorderPane buildHeaders() {
        BorderPane headerRow = new BorderPane();

        Text attackerHeader = new Text();
        attackerHeader.setFont(HudView.HEADER_FONT);
        attackerHeader.setText("Attacker");

        Text defenderHeader = new Text();
        defenderHeader.setFont(HudView.HEADER_FONT);
        defenderHeader.setText("Defender");

        headerRow.setLeft(attackerHeader);
        headerRow.setRight(defenderHeader);

        return headerRow;
    }
}
