package tbs_game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tbs_game.game.Game;
import tbs_game.gui.GameGUI;
import tbs_game.player.Player;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class Main extends Application {

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) {
        Game game = setUpGame();
        GameGUI gui = new GameGUI(game);

        Scene scene = new Scene(gui.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Budget Polytopia");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public Game setUpGame() {
        Game game = new Game(8, 8);

        // Line of soldiers
        for (int i = 0; i < 8; i++) {
            Unit unit = new Unit(UnitType.SOLDIER, Player.USER);
            Unit aiUnit = new Unit(UnitType.SOLDIER, Player.AI);
            game.placeUnitAt(new Position(i, 7), unit);
            game.placeUnitAt(new Position(i, 0), aiUnit);
        }

        return game;
    }
}
