package de.thm.informatik.chess.domain;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class UciParser {

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

            if (!board.isMoveLegal(move, false)) {
                System.out.println("Illegal move: " + from + to);
                break;
            }

            if (whiteToMove) {
                sb.append(moveNumber).append("");
                moveNumber++;
            }

            Piece movingPiece = board.getPiece(fromSquare);
            String pieceSymbol = getPieceSymbol(movingPiece);

            if (pieceSymbol.isEmpty()) {
                //Bauer nur Zielfeld
                sb.append(to.toLowerCase()).append("");
            } else {
                //Figuren Symbol + Zielfeld
                sb.append(pieceSymbol).append(to.toLowerCase()).append("");
            }

            board.doMove(move);
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
            default: return ""; // Pawn
        }
    }

}
