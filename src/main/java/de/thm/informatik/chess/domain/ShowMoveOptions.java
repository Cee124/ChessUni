package de.thm.informatik.chess.domain;

import java.util.ArrayList;
import java.util.List;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class ShowMoveOptions {
    
    private ChessEngine engine;

    public ShowMoveOptions(ChessEngine engine) {
        this.engine = engine;
    }

    public List<Square> getLegalTargetSquares(Square from) {
        List<Move> allMoves = engine.getLegalMoves();
        List<Square> targets = new ArrayList<>();

        for (Move move : allMoves) {
            if (move.getFrom() == from) {
                targets.add(move.getTo());
            }
        }

        return targets;
    }
}






