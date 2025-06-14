package de.thm.informatik.chess.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class UciParser {

    private static final Logger logger = LogManager.getLogger(UciParser.class);

    //Methode nimmt Uci Zugfolge und wandelt in San um
    public static String convertUciToAnnotatedMoves(String uciMoves) {
        //Initialisierung der benötigten Objekte und Variablen
        Board board = new Board();
        StringBuilder sb = new StringBuilder();
        int moveNumber = 1;
        boolean whiteToMove = true;

        //Uci String wird in 4er Blöcke aufgeteilt also from-to Darstellung(e2e4)
        for (int i = 0; i <= uciMoves.length() - 4; i += 4) {
            //Parsen der from Position, erste 2 Zeichen
            String from = uciMoves.substring(i, i + 2).toUpperCase();
            //Parsen der to Position, 2 auf from folgenden Zeichen
            String to = uciMoves.substring(i + 2, i + 4).toUpperCase();

            //Wandelt Positionen in Square Objekte um
            Square fromSquare = Square.fromValue(from);
            Square toSquare = Square.fromValue(to);
            //Erstellt aus Square Objekten Zug mit from-to
            Move move = new Move(fromSquare, toSquare);

            //Wenn der Zug nicht zulässig ist wird eine Fehlermeldung ausgegben und aus der Schleife gebreaked
            if (!board.isMoveLegal(move, false)) {
                logger.info("Illegal Move: " + from + to);
                break;
            }
            //Prüft ob Zug von weiß ist, dann wird Zugnummer vorangeschrieben und hochgezählt
            if (whiteToMove) {
                sb.append(moveNumber).append("");
                moveNumber++;
            }

            //Erstellt Piece Objekt von Objekt auf Startposition
            Piece movingPiece = board.getPiece(fromSquare);
            //Erkennt Kürzel für aktuelles Piece und speichert es in Variable
            String pieceSymbol = getPieceSymbol(movingPiece);

            //Wenn das Piece ein Bauer ist dann wird nur die to Position gespeichert
            if (pieceSymbol.isEmpty()) {
                sb.append(to.toLowerCase()).append("");
            //Wenn Piece != Bauer dann wird Kürzel für Piece for to Position geschrieben
            } else {
                sb.append(pieceSymbol).append(to.toLowerCase()).append("");
            }

            board.doMove(move);
            //Damit nur vor den weißen Zügen eine Zugnummer davor steht sonst wäre schwarzer Zug ein extra Zug
            whiteToMove = !whiteToMove;
        }

        return sb.toString().trim();
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
