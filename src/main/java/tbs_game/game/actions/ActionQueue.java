package tbs_game.game.actions;

import java.util.ArrayDeque;
import java.util.Queue;

import tbs_game.gui.GameGUI;

public class ActionQueue {

    private final Queue<Action> queue = new ArrayDeque<>();

    public ActionQueue() {

    }

    public void addAction(Action act) {
        queue.add(act);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void performNextAction(GameGUI gui, Runnable onComplete) {
        if (isEmpty()) {
            return;
        }

        Action act = queue.poll();
        if (act == null) {
            return;
        }

        act.execute(gui, onComplete);
    }
}
