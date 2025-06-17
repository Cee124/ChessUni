package de.thm.informatik.chess.domain;

import com.github.bhlangonijr.chesslib.Board;

public class GameState {

 private Board board;
 private int moveIndex;
 private long whiteTime;
 private long blackTime;

 public GameState(Board board, int moveIndex, long whiteTime, long blackTime) {
     this.board = board;
     this.moveIndex = moveIndex;
     this.whiteTime = whiteTime;
     this.blackTime = blackTime;
 }

 public Board getBoard() {
     return board;
 }

 public int getMoveIndex() {
     return moveIndex;
 }

 public long getWhiteTime() {
     return whiteTime;
 }

 public long getBlackTime() {
     return blackTime;
 }
}
