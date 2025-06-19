package de.thm.informatik.chess.domain;

import java.util.ArrayList;
import java.util.List;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;

public class GameState {

	private Board board;
	private int moveIndex;
	private long whiteTime;
	private long blackTime;
	private final Side sideToMove;
	private final List<Piece> whiteFallenPieces;
    private final List<Piece> blackFallenPieces;

	public GameState(Board board, int moveIndex, long whiteTime, long blackTime, Side sideToMove, 
						List<Piece> whiteFallenPieces, List<Piece> blackFallenPieces) {
		this.board = board;
		this.moveIndex = moveIndex;
		this.whiteTime = whiteTime;
		this.blackTime = blackTime;
		this.sideToMove = sideToMove;
		this.whiteFallenPieces = new ArrayList<>(whiteFallenPieces);
		this.blackFallenPieces = new ArrayList<>(blackFallenPieces);
		
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

	public List<Piece> getWhiteFallenPieces() {
        return whiteFallenPieces;
    }

    public List<Piece> getBlackFallenPieces() {
        return blackFallenPieces;
    }
}
