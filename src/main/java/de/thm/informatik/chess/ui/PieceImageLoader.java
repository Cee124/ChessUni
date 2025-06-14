package de.thm.informatik.chess.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Piece;


public class PieceImageLoader {
    private static final Map<Piece, Image> pieceImages = new EnumMap<>(Piece.class);
    private static final Logger logger = LogManager.getLogger(PieceImageLoader.class);

    static {
        try {
            pieceImages.put(Piece.WHITE_PAWN, ImageIO.read(PieceImageLoader.class.getResource("/images/wp.png")));
            pieceImages.put(Piece.WHITE_KNIGHT, ImageIO.read(PieceImageLoader.class.getResource("/images/wn.png")));
            pieceImages.put(Piece.WHITE_BISHOP, ImageIO.read(PieceImageLoader.class.getResource("/images/wb.png")));
            pieceImages.put(Piece.WHITE_ROOK, ImageIO.read(PieceImageLoader.class.getResource("/images/wr.png")));
            pieceImages.put(Piece.WHITE_QUEEN, ImageIO.read(PieceImageLoader.class.getResource("/images/wq.png")));
            pieceImages.put(Piece.WHITE_KING, ImageIO.read(PieceImageLoader.class.getResource("/images/wk.png")));

            pieceImages.put(Piece.BLACK_PAWN, ImageIO.read(PieceImageLoader.class.getResource("/images/bp.png")));
            pieceImages.put(Piece.BLACK_KNIGHT, ImageIO.read(PieceImageLoader.class.getResource("/images/bn.png")));
            pieceImages.put(Piece.BLACK_BISHOP, ImageIO.read(PieceImageLoader.class.getResource("/images/bb.png")));
            pieceImages.put(Piece.BLACK_ROOK, ImageIO.read(PieceImageLoader.class.getResource("/images/br.png")));
            pieceImages.put(Piece.BLACK_QUEEN, ImageIO.read(PieceImageLoader.class.getResource("/images/bq.png")));
            pieceImages.put(Piece.BLACK_KING, ImageIO.read(PieceImageLoader.class.getResource("/images/bk.png")));
        } catch (IOException e) {
            logger.debug("Fehler beim Laden der Figurenbilder");
        }
    }

    public static Image getImage(Piece piece) {
        return pieceImages.get(piece);
    }
}



