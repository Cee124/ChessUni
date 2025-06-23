package de.thm.informatik.chess.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class UciParser {

    private static final Logger logger = LogManager.getLogger(UciParser.class);

    // Methode nimmt Uci Zugfolge und wandelt in San um
    public static String convertUciToAnnotatedMoves(String uciMoves) {
        // Initialisierung der benötigten Objekte und Variablen
        Board board = new Board();
        StringBuilder sb = new StringBuilder();
        int moveNumber = 1;
        boolean whiteToMove = true;

        for (int i = 0; i < uciMoves.length();) {
            // Basis: from-to
            String from = uciMoves.substring(i, i + 2).toUpperCase();
            String to = uciMoves.substring(i + 2, i + 4).toUpperCase();

            // Promotion prüfen
            String promo = "";
            if (i + 5 <= uciMoves.length()) {
                char promoChar = uciMoves.charAt(i + 4);
                if ("qrbn".indexOf(promoChar) != -1) {
                    promo = String.valueOf(promoChar).toLowerCase();
                    i += 5; // 5 Zeichen bei Promotion
                } else {
                    i += 4;
                }
            } else {
                i += 4;
            }
            Square fromSquare = Square.fromValue(from);
            Square toSquare = Square.fromValue(to);
            Move move = promo.isEmpty()
            ? new Move(fromSquare, toSquare)
            : new Move(fromSquare, toSquare, getPromotionPiece(promo.charAt(0), board.getSideToMove()));;


            // Wenn der Zug nicht zulässig ist wird eine Fehlermeldung ausgegben und aus der
            // Schleife gebreaked
            if (!board.isMoveLegal(move, false)) {
                logger.debug("Illegal Move: " + from + to);
                break;
            }
            // Prüft ob Zug von weiß ist, dann wird Zugnummer vorangeschrieben und
            // hochgezählt
            if (whiteToMove) {
                sb.append(moveNumber).append("");
                moveNumber++;
            }

            // Erstellt Piece Objekt von Objekt auf Startposition
            Piece movingPiece = board.getPiece(fromSquare);
            // Erkennt Kürzel für aktuelles Piece und speichert es in Variable
            String pieceSymbol = getPieceSymbol(movingPiece);

            // Wenn das Piece ein Bauer ist dann wird nur die to Position gespeichert
            if (pieceSymbol.isEmpty()) {
                sb.append(to.toLowerCase()).append("");
                // Wenn Piece != Bauer dann wird Kürzel für Piece for to Position geschrieben
            } else {
                sb.append(pieceSymbol).append(to.toLowerCase()).append("");
            }

            board.doMove(move);
            // Damit nur vor den weißen Zügen eine Zugnummer davor steht sonst wäre
            // schwarzer Zug ein extra Zug
            whiteToMove = !whiteToMove;
        }

        return sb.toString().trim();
    }

    private static Piece getPromotionPiece(char promoChar, Side side) {
    switch (promoChar) {
        case 'q':
            return side == Side.WHITE ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
        case 'r':
            return side == Side.WHITE ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
        case 'b':
            return side == Side.WHITE ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
        case 'n':
            return side == Side.WHITE ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
        default:
            throw new IllegalArgumentException("Ungültige Promotion: " + promoChar);
    }
}

    // Methode um Darstellung in San Formatierung umzuwandeln
    private static String getPieceSymbol(Piece piece) {
        switch (piece.getPieceType()) {
            case KNIGHT:
                return "n";
            case BISHOP:
                return "b";
            case ROOK:
                return "r";
            case QUEEN:
                return "q";
            case KING:
                return "k";
            default:
                return ""; // Bauer
        }
    }

}
