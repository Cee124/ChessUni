import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.github.bhlangonijr.chesslib.Square;
import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.ShowMoveOption;

public class TestShowMoveOption {

    private ChessEngine engine;
    private ShowMoveOption showMoveOption;

    @BeforeEach
    void setup() {
        this.engine = new ChessEngine();
        this.showMoveOption = new ShowMoveOption(engine);

    }

    @Test
    void testGetLegalTargetSquares_WhiteKnight() {
    Square whiteKnight = Square.B1;
    List<Square> expectedTargetSquaresWhiteKnight = Arrays.asList(Square.A3, Square.C3);
    List<Square> actualTargetSquaresWhiteKnight = showMoveOption.getLegalTargetSquares(whiteKnight);
    //Die Liste erstellt durch die Methode enthält soll am Anfang genau die Werte enthalten
    assertTrue(actualTargetSquaresWhiteKnight.containsAll(expectedTargetSquaresWhiteKnight));
    //Die Liste hat nur die Ziele, die sie auch haben soll
    assertEquals(expectedTargetSquaresWhiteKnight.size(), actualTargetSquaresWhiteKnight.size());
    }

    @Test
    void testGetLegalTargetSquares_WhitePawn() {
    Square whitePawn = Square.E2;
    List<Square> expectedTargetSquaresWhitePawn = Arrays.asList(Square.E3, Square.E4);
    List<Square> actualTargetSquaresWhitePawn = showMoveOption.getLegalTargetSquares(whitePawn);
    
    assertTrue(actualTargetSquaresWhitePawn.containsAll(expectedTargetSquaresWhitePawn));
    assertEquals(expectedTargetSquaresWhitePawn.size(), actualTargetSquaresWhitePawn.size());
    }

    @Test 
    void testGetLegalTargetSquares_WhiteQueen() {

    Square whiteQueen = Square.D1;
   
    List<Square> actualTargetSquaresWhiteQueen = showMoveOption.getLegalTargetSquares(whiteQueen);
    //Liste ist am Anfang leer, da die weiße Dame sich nicht bewegen kann
    assertTrue(actualTargetSquaresWhiteQueen.isEmpty());
    }

    @Test 
    void testGetLegalTargetSquares_BlackKnight() {
        Square blackKnight = Square.B8;
        List<Square> actualTargetSquaresBlackKnight = showMoveOption.getLegalTargetSquares(blackKnight);
        //Liste ist leer, da weiß beginnt und schwarz keinen legalen Zug machen kann
        assertTrue(actualTargetSquaresBlackKnight.isEmpty());


    }


    

}
