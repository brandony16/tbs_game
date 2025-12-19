package tbs_game.game;

import tbs_game.board.Board;

public class Game {
  private Board board;
  
  public Game(int width, int height) {
    this.board = new Board(width, height);
  }

  public Board getBoard() {
    return board;
  }
}
