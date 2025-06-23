package de.thm.informatik.chess.ui;

import java.util.List;

import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.Facade;

public class SkipHandler {
    private final ChessEngine engine;
    private ChessPanel panel;
    private ClockHandler handler;
    private Facade facade;

    public SkipHandler(ChessEngine engine, Facade facade) {
        this.engine = engine;
        this.facade = facade;
    }

    public void setPanel(ChessPanel panel) {
        this.panel = panel;
    }

    public void setHandler(ClockHandler handler) {
        this.handler = handler;
    }

    public void rewindMove() {
        if(!panel.rewindSelectedPanel){
            return;
        }
        List<Move> moveHistory = facade.getMoveHistory();
        int currentMoveIndex = panel.getCurrentMoveIndex();
        
        if (currentMoveIndex > 0) {
            panel.setCurrentMoveIndex(--currentMoveIndex);
            engine.reset();
            
            for (int i = 0; i < currentMoveIndex; i++) {
                engine.makeMove(moveHistory.get(i));
            }
            handler.updateClocks();
            panel.repaint();
        }
    }

    public void fastForwardMove() {
        List<Move> moveHistory = facade.getMoveHistory();
        int currentMoveIndex = panel.getCurrentMoveIndex();

        if (currentMoveIndex < moveHistory.size()) {
            Move forwardMove = moveHistory.get(currentMoveIndex);
            engine.makeMove(forwardMove);
            panel.setCurrentMoveIndex(currentMoveIndex + 1);
            handler.updateClocks();
            panel.repaint();
        }
    }
}