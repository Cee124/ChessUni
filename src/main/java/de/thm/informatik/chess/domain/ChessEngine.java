package de.thm.informatik.chess.domain;

import java.util.LinkedList;
import java.util.List;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class ChessEngine {

    private Board board;
    private final List<Move> moveHistory = new LinkedList<>();
    public ChessEngine() {
        this.board = new Board();
    }
    
    public ChessEngine(Board customBoard) {
    	this.board = customBoard;
    }

    public List<Move> getMoveHistory() {
    return moveHistory;
    }

    public Board getBoard() {
        return board;
    }
    
    public void setBoard(Board newBoard) {
        this.board = newBoard;
    }

    public List<Move> getLegalMoves() {
        return board.legalMoves();
    }

    public boolean makeMove(Move move) {
        if (getLegalMoves().contains(move)) {
            board.doMove(move);
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return board.isMated() || board.isDraw();
    }

    public boolean isCheckmate() {
        return board.isMated();
    }

    public boolean isInCheck() {
        return board.isKingAttacked();
    }

    public void reset() {
        board = new Board();
    }

    public Piece getPiece(Square square) {
        return board.getPiece(square);
    }

}
