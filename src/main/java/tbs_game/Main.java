package tbs_game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tbs_game.game.Game;
import tbs_game.gui.GameGUI;

public class Main extends Application {

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) {
        Game game = new Game(4);
        GameGUI gui = new GameGUI(game);

        Scene scene = new Scene(gui.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Budget Polytopia");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
