import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;

import de.thm.informatik.chess.domain.GameState;


public class TestGameState {

    private Board board;
    private int moveIndex;
    private long whiteTime;
    private long blackTime;
    private Side sideToMove;
    private List<Piece> whiteFallenPieces;
    private List<Piece> blackFallenPieces;


    @BeforeEach
    void setUp() {
        board = new Board();
        moveIndex = 10;
        whiteTime = 5000L;
        blackTime = 4000L;
        sideToMove = Side.WHITE;

        whiteFallenPieces = new ArrayList<>();
        whiteFallenPieces.add(Piece.WHITE_PAWN);
        whiteFallenPieces.add(Piece.WHITE_BISHOP);

        blackFallenPieces = new ArrayList<>();
        blackFallenPieces.add(Piece.BLACK_QUEEN);
    }

    @Test 
    void testGameStateInitializer() {
        GameState gameState = new GameState(board, 
        moveIndex, whiteTime, blackTime, 
        sideToMove, whiteFallenPieces, blackFallenPieces);

        //Getter testen
        assertEquals(board, gameState.getBoard());
        assertEquals(moveIndex, gameState.getMoveIndex());
        assertEquals(whiteTime, gameState.getWhiteTime());
        assertEquals(blackTime, gameState.getBlackTime());
        assertEquals(sideToMove, gameState.getSideToMove());
        assertEquals(whiteFallenPieces, gameState.getWhiteFallenPieces());
        assertEquals(blackFallenPieces, gameState.getBlackFallenPieces());
    }



    @Test
    void testBoardStateIsStoredCorrectly() {
        Board board = new Board();
        board.doMove("e2e4");

        GameState state = new GameState(board, moveIndex, whiteTime, blackTime,
                Side.BLACK, new ArrayList<>(), new ArrayList<>()
        );

        //Prüfe, ob sich der weiße Bauer korrekt auf e4 befindet
        assertEquals(Piece.WHITE_PAWN, state.getBoard().getPiece(Square.E4));

        //Stelle sicher, dass e2 jetzt leer ist
        assertEquals(Piece.NONE, state.getBoard().getPiece(Square.E2));

        //Stelle sicher, dass Schwarz am Zug ist
        assertEquals(Side.BLACK, state.getBoard().getSideToMove());
    }


    @Test
    void testFallenPiecesAreCopied() {
        List<Piece> whiteFallen = new ArrayList<>();
        whiteFallen.add(Piece.WHITE_PAWN);

        List<Piece> blackFallen = new ArrayList<>();
        blackFallen.add(Piece.BLACK_ROOK);

        GameState gameState = new GameState(new Board(), moveIndex, whiteTime, blackTime, 
        Side.WHITE, whiteFallen, blackFallen);

        //Original Listen nachträglich verändern
        whiteFallen.add(Piece.WHITE_KNIGHT);
        blackFallen.add(Piece.BLACK_KING);

        //Interne Kopien dürfen nicht verändert worden sein!
        assertEquals(1, gameState.getWhiteFallenPieces().size());
        assertEquals(1, gameState.getBlackFallenPieces().size());
        assertFalse(gameState.getWhiteFallenPieces().contains(Piece.WHITE_KNIGHT));
        assertFalse(gameState.getBlackFallenPieces().contains(Piece.BLACK_KING));
    }
}




