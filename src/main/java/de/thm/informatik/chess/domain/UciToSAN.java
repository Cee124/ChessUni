package de.thm.informatik.chess.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class UciToSAN {

    private static final Logger logger = LogManager.getLogger(UciParser.class);

    //Methode nimmt Uci Zugfolge und wandelt in San um
    public static String convertUciToAnnotatedMoves(String uciMoves) {
        Board board = new Board();
        StringBuilder sb = new StringBuilder();
        int moveNumber = 1;
        boolean whiteToMove = true;

        for (int i = 0; i <= uciMoves.length() - 4; i += 4) {
            String from = uciMoves.substring(i, i + 2).toUpperCase();
            String to = uciMoves.substring(i + 2, i + 4).toUpperCase();

            Square fromSquare = Square.fromValue(from);
            Square toSquare = Square.fromValue(to);
            Move move = new Move(fromSquare, toSquare);

            Piece movingPiece = board.getPiece(fromSquare);
            Piece capturedPiece = board.getPiece(toSquare);

            if (!board.isMoveLegal(move, false)) {
                logger.debug("Illegal Move: " + from + to);
                break;
            }

            // Zugnummer und Punkt nur für Weiß
            if (whiteToMove) {
                sb.append(moveNumber).append(".");
                moveNumber++;
            }

            String sanMove;

            // Rochade erkennen
            if (movingPiece.getPieceType().equals(com.github.bhlangonijr.chesslib.PieceType.KING) &&
                    (from.equals("E1") || from.equals("E8"))) {
                if (to.equals("G1") || to.equals("G8")) {
                    sanMove = "O-O";
                } else if (to.equals("C1") || to.equals("C8")) {
                    sanMove = "O-O-O";
                } else {
                    sanMove = getSanPieceMove(movingPiece, fromSquare, toSquare, capturedPiece);
                }
            } else {
                sanMove = getSanPieceMove(movingPiece, fromSquare, toSquare, capturedPiece);
            }

            sb.append(sanMove).append(" ");
            board.doMove(move);
            whiteToMove = !whiteToMove;
        }

        return sb.toString().trim();
    }

    private static String getSanPieceMove(Piece movingPiece, Square fromSquare, Square toSquare, Piece capturedPiece) {
        String from = fromSquare.toString().toLowerCase();
        String to = toSquare.toString().toLowerCase();

        String pieceSymbol = getPieceSymbol(movingPiece);
        boolean isPawn = pieceSymbol.isEmpty();
        boolean isCapture = capturedPiece != Piece.NONE;

        if (isPawn) {
            if (isCapture) {
                // Bauer schlägt (z. B. exd5)
                return from.charAt(0) + "x" + to;
            } else {
                return to;
            }
        } else {
            // Figur schlägt oder normal zieht
            return pieceSymbol.toUpperCase() + (isCapture ? "x" : "") + to;
        }
    }

    //Methode um Darstellung in San Formatierung umzuwandeln
    private static String getPieceSymbol(Piece piece) {
        switch (piece.getPieceType()) {
            case KNIGHT: return "n";
            case BISHOP: return "b";
            case ROOK: return "r";
            case QUEEN: return "q";
            case KING: return "k";
            default: return ""; //Bauer
        }
    }

}