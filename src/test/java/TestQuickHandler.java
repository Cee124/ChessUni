import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.Facade;
import de.thm.informatik.chess.domain.GameState;
import de.thm.informatik.chess.domain.QuickHandler;
import de.thm.informatik.chess.ui.ClockHandler;
import de.thm.informatik.chess.ui.FallenPiecesHandler;

public class TestQuickHandler {
    private ChessEngine engine;
    private ClockHandler handlerC;
    private FallenPiecesHandler handlerFP;
    private List<Piece> whiteFallen;
    private List<Piece> blackFallen;
    private List<Move> moveHistory;
    private QuickHandler handlerQ;
    private Facade facade;
    @BeforeEach
    void setup(){
        engine = new ChessEngine();
        handlerC = new ClockHandler();
        whiteFallen = new ArrayList<>(List.of(Piece.WHITE_BISHOP));
        blackFallen = new ArrayList<>(List.of(Piece.BLACK_QUEEN, Piece.BLACK_KNIGHT));
        moveHistory = new ArrayList<>();

        handlerFP = new FallenPiecesHandler(whiteFallen, blackFallen, 0, true, facade);

        engine.getBoard().loadFromFen("rn1qkbnr/pp3ppp/2p5/3pp3/4P3/2N2N2/PPP2PPP/R1BQKB1R w KQkq - 0 1");

        handlerC.setWhiteRemaining(300_000);
        handlerC.setBlackRemaining(300_000);

        handlerQ = new QuickHandler(engine, handlerC, whiteFallen, blackFallen, 5, moveHistory, handlerFP);
    }

    @Test
    void testQuickSave(){
        handlerQ.quicksave();
        GameState saved = handlerQ.getQuickSaveState();

        //Prüfen ob Objekt korrekt erstellt wurde
        assertNotNull(saved);
        //Inhalte des Objekts auf Korrektheit prüfen
        assertEquals(5, saved.getMoveIndex());
        assertEquals(300_000L, saved.getWhiteTime());
        assertEquals(300_000L, saved.getBlackTime());
        assertEquals(Side.WHITE, saved.getBoard().getSideToMove());
        assertEquals(List.of(Piece.WHITE_BISHOP), saved.getWhiteFallenPieces());
        assertEquals(List.of(Piece.BLACK_QUEEN, Piece.BLACK_KNIGHT), saved.getBlackFallenPieces());
    }

    @Test
    void testQuickLoad(){
        handlerQ.quicksave();

        //Test ob nach manipulation der Daten GameState korrekt ist
        whiteFallen.clear();
        blackFallen.clear();

        handlerQ.quickload();

        assertEquals(1, whiteFallen.size());
        assertEquals(2, blackFallen.size());

        assertEquals(300_000L, handlerC.getWhiteRemaining());
        assertEquals(300_000L, handlerC.getBlackRemaining());

        assertTrue(engine.getBoard().getSideToMove() == Side.WHITE);
    }

    @Test
    void testEmptyQuickLoad(){
        handlerQ.quicksave();

        //Test Objekte hinzufügen um korrekten Stand prüfen zu können
        whiteFallen.add(Piece.WHITE_PAWN);
        blackFallen.add(Piece.BLACK_ROOK);
        
        handlerQ.quickload();

        assertEquals(1, whiteFallen.size());
        assertEquals(2, blackFallen.size());

        assertEquals(300_000L, handlerC.getWhiteRemaining());
        assertEquals(300_000L, handlerC.getBlackRemaining());
    }
}
