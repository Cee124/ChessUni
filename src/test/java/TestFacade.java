import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.Facade;
import de.thm.informatik.chess.ui.ClockHandler;
import de.thm.informatik.chess.ui.FallenPiecesHandler;
import static org.junit.jupiter.api.Assertions.*;

public class TestFacade {

    private Facade facade;

    @BeforeEach
    public void setUp() throws IOException {
       
        ClockHandler clockHandler = new ClockHandler();
        ArrayList<Piece> whiteFallenPieces = new ArrayList<>();
        ArrayList<Piece> blackFallenPieces = new ArrayList<>();
        FallenPiecesHandler fallenPiecesHandler = new FallenPiecesHandler(blackFallenPieces, blackFallenPieces, 
        0, false, facade);
        int currentMoveIndex = 0;

        facade = new Facade(clockHandler, whiteFallenPieces, blackFallenPieces, currentMoveIndex, fallenPiecesHandler);
    }

    @Test
    public void testGetPieceAt_startPosition() {
        Piece piece = facade.getPieceAt(Square.E2);
        assertEquals(Piece.WHITE_PAWN, piece);
    }

    @Test
    public void testIsGameOverAtStart() {
        assertFalse(facade.isGameOver());
    }

    @Test
    public void testIsCheckmateAtStart() {
        assertFalse(facade.isCheckmate());
    }

    @Test
    public void testIsInCheckAtStart() {
        assertFalse(facade.isInCheck());
    }

    @Test
    public void testMakeLegalMove() {
    Move move = new Move(Square.E2, Square.E4);
    assertTrue(facade.makeMove(move));
    }

    @Test
    public void testMakeIllegalMove() {
    Move move = new Move(Square.E2, Square.E5); // Kein legaler Zug
    assertFalse(facade.makeMove(move));
    }

    @Test
    public void testLegalTargetSquaresFromE2() {    
    List<Square> targets = facade.getLegalTargetSquares(Square.E2);
    assertTrue(targets.contains(Square.E3) || targets.contains(Square.E4));
    }

    @Test
    public void testResetBoard() {
        facade.makeMove(new Move(Square.E2, Square.E4)); 
        facade.resetBoard(); 
        Piece piece = facade.getPieceAt(Square.E2);
        assertEquals(Piece.WHITE_PAWN, piece); 
    }

    @Test
    public void testGetOpeningName_invalidInput() {
        assertNull(facade.getOpeningName("Schachhhhh"));
    }

    @Test
    public void testGetMoveHistory() {
        assertNotNull(facade.getMoveHistory());
    }

    @Test
    public void testSetAndGetBoard() {
        Board board = facade.getBoard();
        facade.setBoard(board);
        assertEquals(board, facade.getBoard());
    }
}
