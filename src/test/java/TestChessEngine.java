import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;

public class TestChessEngine {

    private ChessEngine engine;

    @BeforeEach
    void setup(){
        engine = new ChessEngine();
    }

    @Test
    void testBoardSetUp(){
        //Prüfen ob Brett korrekt initialisiert
        assertNotNull(engine.getBoard());
        assertFalse(engine.isGameOver());
        assertFalse(engine.isCheckmate());
        assertFalse(engine.isInCheck());
    }
    

    @Test
    void testGetPiece(){
        //teste anfangspositionen weiss
        assertEquals(Piece.WHITE_ROOK, engine.getPiece(Square.A1));
        assertEquals(Piece.WHITE_KNIGHT, engine.getPiece(Square.B1));
        assertEquals(Piece.WHITE_BISHOP, engine.getPiece(Square.C1));
        assertEquals(Piece.WHITE_QUEEN, engine.getPiece(Square.D1));
        assertEquals(Piece.WHITE_KING, engine.getPiece(Square.E1));
        assertEquals(Piece.WHITE_BISHOP, engine.getPiece(Square.F1));
        assertEquals(Piece.WHITE_KNIGHT, engine.getPiece(Square.G1));
        assertEquals(Piece.WHITE_ROOK, engine.getPiece(Square.H1));
        //Test für alle weissen Bauern
        char[] rows = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        for(char row : rows){
            String comb = row + String.valueOf(2);
            Square square = Square.fromValue(comb);
            assertEquals(Piece.WHITE_PAWN, engine.getPiece(square));
        }

        //teste anfangspositionen schwarz
        assertEquals(Piece.BLACK_ROOK, engine.getPiece(Square.A8));
        assertEquals(Piece.BLACK_KNIGHT, engine.getPiece(Square.B8));
        assertEquals(Piece.BLACK_BISHOP, engine.getPiece(Square.C8));
        assertEquals(Piece.BLACK_QUEEN, engine.getPiece(Square.D8));
        assertEquals(Piece.BLACK_KING, engine.getPiece(Square.E8));
        assertEquals(Piece.BLACK_BISHOP, engine.getPiece(Square.F8));
        assertEquals(Piece.BLACK_KNIGHT, engine.getPiece(Square.G8));
        assertEquals(Piece.BLACK_ROOK, engine.getPiece(Square.H8));
        //Test für alle schwarzen Bauern
        for(char row : rows){
            String comb = row + String.valueOf(7);
            Square square = Square.fromValue(comb);
            assertEquals(Piece.BLACK_PAWN, engine.getPiece(square));
        }
    }

    @Test
    void testLegalMoves(){
        //20 legale Züge verfügbar am Anfang
        assertEquals(20, engine.getLegalMoves().size());
    }

    @Test
    void testMakeLegalMove(){
        Move e2e4 = new Move(Square.E2, Square.E4);
        assertTrue(engine.makeMove(e2e4));
        assertEquals(Piece.WHITE_PAWN, engine.getPiece(Square.E4));
        assertEquals(Piece.NONE, engine.getPiece(Square.E2));
    }

    @Test
    void testMakeIllegalMove(){
        //Illegal, Bauer darf nur 2 Felder vor nicht 3
        Move e2e5 = new Move(Square.E2, Square.E5);
        assertFalse(engine.makeMove(e2e5));
        assertEquals(Piece.WHITE_PAWN, engine.getPiece(Square.E2));
        assertEquals(Piece.NONE, engine.getPiece(Square.E5));
    }

    @Test
    void testReset(){
        engine.makeMove(new Move(Square.E2, Square.E4));
        engine.makeMove(new Move(Square.E7, Square.E5));

        engine.reset();

        //Prüfen ob Bauern wieder auf Startpositionen
        assertEquals(Piece.WHITE_PAWN, engine.getPiece(Square.E2));
        assertEquals(Piece.BLACK_PAWN, engine.getPiece(Square.E7));
        assertFalse(engine.isGameOver());
    }

    @Test
    void testCheckDetection(){
        //erstelle Positionen in der weiss im Schach steht
        Board customBoard = new Board();
        customBoard.loadFromFen("4k3/8/8/8/8/8/4r3/4K3 w - - 0 1");

        ChessEngine customEngine = new ChessEngine(customBoard);

        //weiss steht im Schach ist jedoch nicht Matt also spielt nicht vorbei
        assertTrue(customEngine.isInCheck());
        assertFalse(customEngine.isCheckmate());
        assertFalse(customEngine.isGameOver());
    }

    @Test
    void testCheckMateDetection(){
        //erstelle Position in der weiss Schachmatt ist
        Board customBoard = new Board();
        customBoard.loadFromFen("8/8/8/8/8/8/6kq/7K w - - 0 1");

        ChessEngine mateEngine = new ChessEngine(customBoard);

        //weiss ist Matt also alles true
        assertTrue(mateEngine.isCheckmate());
        assertTrue(mateEngine.isInCheck());
        assertTrue(mateEngine.isGameOver());
    }
}
