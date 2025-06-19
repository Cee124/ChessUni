package de.thm.informatik.chess.domain;

import java.util.List;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Side;
public class ChessEngine {

    private Board board;

    public ChessEngine() {
        board = new Board();
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

    public boolean isStalemate() {
        return board.isStaleMate();
    }

    public String getWinner() {
        if (isCheckmate()) {
            Side loser = board.getSideToMove(); // der am Zug ist, ist matt
            return loser == Side.WHITE ? "Schwarz" : "Weiß";
        }
        return null;
    }

    public void reset() {
        board = new Board();
    }

    public Piece getPiece(Square square) {
        return board.getPiece(square);
    }

}
