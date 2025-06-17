package de.thm.informatik.chess.domain;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;

public class GameState {

	private Board board;
	private int moveIndex;
	private long whiteTime;
	private long blackTime;
	private final Side sideToMove;

	public GameState(Board board, int moveIndex, long whiteTime, long blackTime, Side sideToMove) {
		this.board = board;
		this.moveIndex = moveIndex;
		this.whiteTime = whiteTime;
		this.blackTime = blackTime;
		this.sideToMove = sideToMove;
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
	
    public Side getSideToMove() {
        return sideToMove;
    }

}
