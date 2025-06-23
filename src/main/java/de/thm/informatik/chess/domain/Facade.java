package de.thm.informatik.chess.domain;

import java.awt.Image;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.service.OpeningDetection;
import de.thm.informatik.chess.ui.ClockHandler;
import de.thm.informatik.chess.ui.FallenPiecesHandler;
import de.thm.informatik.chess.util.PieceIconLoader;
import de.thm.informatik.chess.util.PieceImageLoader;

public class Facade {

    private ChessEngine engine;
    private QuickHandler quickHandler;
    private ShowMoveOption moveOption;
    private OpeningDetection openingDetection;
    private Map<String, String> openingsMap;
    private List<Move> moveHistory;
    public Facade(ClockHandler clockHandler,
            List<Piece> whiteFallenPieces,
            List<Piece> blackFallenPieces,
            int currentMoveIndex,
            FallenPiecesHandler fallenPiecesHandler) throws IOException {

        this.engine = new ChessEngine();
        moveHistory = engine.getMoveHistory();
        this.quickHandler = new QuickHandler(engine, clockHandler,
                whiteFallenPieces, blackFallenPieces,
                currentMoveIndex, moveHistory,
                fallenPiecesHandler);
        this.moveOption = new ShowMoveOption(engine);
        

        this.openingDetection = new OpeningDetection();
        this.openingsMap = openingDetection.loadOpenings("/Openings/eco_openings.html");
    }

    public Image getImage(Piece piece) {
        return PieceImageLoader.getImage(piece);
    }

        public ImageIcon getIcon(String iconName) {
        switch (iconName) {
            case "FORWARD": return PieceIconLoader.FORWARD_ICON;
            case "REWIND": return PieceIconLoader.REWIND_ICON;
            case "START": return PieceIconLoader.START_ICON;
            case "PAUSE": return PieceIconLoader.PAUSE_ICON;
            case "WHITEKING": return PieceIconLoader.WHITEKING_ICONX;
            case "BLACKKING": return PieceIconLoader.BLACKKING_ICONX;
            case "EMPTY": return PieceIconLoader.EMPTY_ICON;
            case "TICKED": return PieceIconLoader.TICKED_ICON;
            case "QUICKSAVE": return PieceIconLoader.QUICKSAVE_ICON;
            case "QUICKLOAD": return PieceIconLoader.QUICKLOAD_ICON;
            default: return null;
        }
    }


    public String getOpeningName(String uciSequence) {
        return openingsMap.get(uciSequence);
    }
    public Map<String, String> getOpeningsMap() {
        return openingsMap;
    }

    
    public OpeningDetection getOpeningDetection() {
        return openingDetection;
    }


    
    public boolean makeMove(Move move) {
        return engine.makeMove(move);
    }

    public boolean isGameOver() {
        return engine.isGameOver();
    }

    public boolean isCheckmate() {
        return engine.isCheckmate();
    }

    public boolean isInCheck() {
        return engine.isInCheck();
    }

    public void resetBoard() {
        engine.reset();
    }

    public Piece getPieceAt(Square square) {
        return engine.getPiece(square);
    }

    public ChessEngine getEngine() {
        return engine;
    }

    public Board getBoard() {
        return engine.getBoard();
    }

    public List<Move> getLegalMoves() {
        return engine.getLegalMoves();
    }

    public List<Move> getMoveHistory() {
        return engine.getMoveHistory();
    }

    
    public List<Square> getLegalTargetSquares(Square from) {
        return moveOption.getLegalTargetSquares(from);
    }

   
    public void quicksave() {
        quickHandler.quicksave();
    }

    public void quickload() {
        quickHandler.quickload();
    }

    public Piece getPiece(Square square) {
        return engine.getPiece(square);
    }

    public GameState getSavedGameState() {
        return quickHandler.getQuickSaveState();
    }

    public void setBoard(Board newBoard) {
        engine.setBoard(newBoard);
    }

    public QuickHandler getQuickHandler() {
        return quickHandler;
    }

    public ShowMoveOption getMoveOption() {
        return moveOption;
    }

}
