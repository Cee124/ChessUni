package de.thm.informatik.chess.domain;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.ui.FallenPiecesHandler;

public class QuickHandler {
     private static final Logger logger = LogManager.getLogger(QuickHandler.class);
    
    private GameState quickSaveState = null;
    private ChessEngine engine;
    private ClockHandler clockHandler;
    private List<Piece> whiteFallenPieces;
    private List<Piece> blackFallenPieces;
    private int currentMoveIndex;
    private List<Move> moveHistory;
    private FallenPiecesHandler fallenPiecesHandler;

    public QuickHandler(ChessEngine engine, ClockHandler clockHandler, 
                       List<Piece> whiteFallenPieces, List<Piece> blackFallenPieces,
                       int currentMoveIndex, List<Move> moveHistory,
                       FallenPiecesHandler fallenPiecesHandler) {
        this.engine = engine;
        this.clockHandler = clockHandler;
        this.whiteFallenPieces = whiteFallenPieces;
        this.blackFallenPieces = blackFallenPieces;
        this.currentMoveIndex = currentMoveIndex;
        this.moveHistory = moveHistory;
        this.fallenPiecesHandler = fallenPiecesHandler;
    }

    public void quicksave() {
        quickSaveState = new GameState(
            engine.getBoard().clone(), 
            currentMoveIndex, 
            clockHandler.getWhiteRemaining(),
            clockHandler.getBlackRemaining(), 
            engine.getBoard().getSideToMove(), 
            whiteFallenPieces, 
            blackFallenPieces
        );
        logger.info("Quicksave durchgefuehrt.");
    }

    public void quickload() {
        if (quickSaveState == null) {
            logger.warn("Kein Quicksave-Zustand vorhanden.");
            return;
        }

        // Board-Zustand direkt aus dem SaveState übernehmen
        engine.setBoard(quickSaveState.getBoard().clone());

        // Move-Index setzen
        currentMoveIndex = quickSaveState.getMoveIndex();

        // SideToMove ist bereits im gespeicherten Board enthalten, daher nicht extra setzen

        // Uhren zurücksetzen
        clockHandler.setWhiteRemaining(quickSaveState.getWhiteTime());
        clockHandler.setBlackRemaining(quickSaveState.getBlackTime());

        // FallenPieces zurücksetzen
        whiteFallenPieces.clear();
        whiteFallenPieces.addAll(quickSaveState.getWhiteFallenPieces());
        blackFallenPieces.clear();
        blackFallenPieces.addAll(quickSaveState.getBlackFallenPieces());
    
        // FallenPiecesHandler aktualisieren
        fallenPiecesHandler.setWhiteFallenPieces(whiteFallenPieces);
        fallenPiecesHandler.setBlackFallenPieces(blackFallenPieces);
    
        clockHandler.startClocks();
        logger.info("Quickload durchgefuehrt.");
}

    public GameState getQuickSaveState() {
        return quickSaveState;
    }
}
