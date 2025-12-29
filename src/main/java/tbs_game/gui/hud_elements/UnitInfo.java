package tbs_game.gui.hud_elements;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tbs_game.units.Unit;

public class UnitInfo {

    private StackPane unitInfo;
    private Text unitInfoText;

    public UnitInfo() {
        initTroopInfoHUD();
    }

    public StackPane getRoot() {
        return this.unitInfo;
    }

    public void updateInfo(Unit unit) {
        if (unit == null) {
            return;
        }

        unitInfoText.setText("Selected Unit: " + unit.getType().name()
                + " HP: " + unit.getHealth()
                + "/" + unit.getType().maxHp);
    }

    public void resetInfo() {
        unitInfoText.setText("");
    }

    public void setVisibility(boolean isVisible) {
        this.unitInfo.setVisible(isVisible);
    }

    private void initTroopInfoHUD() {
        unitInfo = PanelFactory.createHudPanel(300, 200);

        unitInfoText = new Text();
        unitInfoText.setFont(Font.font(16));
        unitInfoText.setWrappingWidth(260);

        StackPane.setAlignment(unitInfoText, Pos.TOP_LEFT);

        unitInfo.getChildren().add(unitInfoText);

        unitInfo.setVisible(false);
    }
}
