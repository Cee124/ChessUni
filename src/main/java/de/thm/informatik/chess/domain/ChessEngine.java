package de.thm.informatik.chess.domain;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;
import java.util.List;

public class ChessEngine {

    private Board board;

    public ChessEngine() {
        board = new Board();
    }

    public Board getBoard() {
        return board;
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

    public void reset() {
        board = new Board();
    }

    public Piece getPiece(Square square) {
        return board.getPiece(square);
    }

}
