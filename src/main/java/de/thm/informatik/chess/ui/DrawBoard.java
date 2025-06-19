package de.thm.informatik.chess.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.util.PieceImageLoader;

public class DrawBoard {
    private ChessEngine engine;
    private int squareSize;
    private boolean color;
    private boolean showMoveOptionsSelectedPanel = false;


    public DrawBoard(ChessEngine engine, int squareSize, boolean color) {
        this.engine = engine;
        this.squareSize = squareSize;
        this.color = color;
    }

    public void drawBoard(Graphics g, List<Square> highlightedSquares) {
        // Draw chess board
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int drawRank = color ? 7 - rank : rank;
				int drawFile = color ? file : 7 - file;

				if ((rank + file) % 2 == 0) {
					g.setColor(Color.lightGray);
				} else {
					g.setColor(Color.white);
				}
				g.fillRect(drawFile * squareSize, drawRank * squareSize, squareSize, squareSize);
			}
		}

        // Highlight-Ziele anzeigen
		if (showMoveOptionsSelectedPanel) {
			for (Square sq : highlightedSquares) {
				int file = color ? sq.getFile().ordinal() : 7 - sq.getFile().ordinal();
				int rank = color ? 7 - sq.getRank().ordinal() : sq.getRank().ordinal();
				g.setColor(new Color(100, 180, 255, 128));
				g.fillRect(file * squareSize, rank * squareSize, squareSize, squareSize);
			}
		}
		// Draw pieces
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int drawRank = color ? 7 - rank : rank;
				int drawFile = color ? file : 7 - file;

				Square sq = squareFromCoords(rank, file);
				Piece piece = engine.getBoard().getPiece(sq);
				if (piece != Piece.NONE) {
					Image img = PieceImageLoader.getImage(piece);
					if (img != null) {
						g.drawImage(img, drawFile * squareSize, drawRank * squareSize, squareSize, squareSize, null);
					}
				}
			}
		}
    }

    public Square squareFromCoords(int rank, int file) {
		char fileChar = (char) ('A' + file);
		char rankChar = (char) ('1' + rank);
		String squareName = "" + fileChar + rankChar;
		return Square.valueOf(squareName);
	}

    public void setShowMoveOptions(boolean enableShowMoveOptions) {
		this.showMoveOptionsSelectedPanel = enableShowMoveOptions;
	}

    public void setColor(boolean color) {
        this.color = color;
    }
}
