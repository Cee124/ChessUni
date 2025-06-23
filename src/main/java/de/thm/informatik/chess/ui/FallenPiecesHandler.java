package de.thm.informatik.chess.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import com.github.bhlangonijr.chesslib.Piece;

import de.thm.informatik.chess.domain.Facade;
import de.thm.informatik.chess.util.PieceImageLoader;

public class FallenPiecesHandler {
    private List<Piece> whiteFallenPieces;
    private List<Piece> blackFallenPieces;
    private int squareSize;
    private boolean color;
	private Facade facade; 
    public FallenPiecesHandler(List<Piece> whiteFallenPieces, List<Piece> blackFallenPieces, int squareSize, boolean color, Facade facade) {
        this.whiteFallenPieces = whiteFallenPieces;
        this.blackFallenPieces = blackFallenPieces;
        this.squareSize = squareSize;
        this.color = color;
		this.facade = facade;
    }

    public void drawFallenPieces(Graphics g) {
        //Stats
		List<Piece> bottomPieces = color ? whiteFallenPieces : blackFallenPieces;
		List<Piece> topPieces = color ? blackFallenPieces : whiteFallenPieces;
        int boardPixelSize = 8 * squareSize;
        int clockX = boardPixelSize + 50;
        // Draw fallen pieces white
		int xPieceWhite = clockX;
		int yPieceWhite = boardPixelSize - 100;
		int countWhite = 0;
		for (Piece p : bottomPieces) {
			countWhite++;
			Image imgP = PieceImageLoader.getImage(p);
			if (imgP != null) {
				g.drawImage(imgP, xPieceWhite, yPieceWhite, 20, 20, null);
			}
			if (countWhite % 6 == 0) {
				yPieceWhite -= 20;
			}
			if (countWhite < 6 && countWhite != 7) {
				xPieceWhite += 15;
			} else if (countWhite > 6 && countWhite != 7 && countWhite < 12) {
				xPieceWhite -= 15;
			} else if (countWhite > 12 && countWhite != 13) {
				xPieceWhite += 15;
			}
			if (countWhite == 7) {
				xPieceWhite -= 15;
			} else if (countWhite == 13) {
				xPieceWhite += 15;
			}
		}

		// Draw fallen pieces black
		int xPieceBlack = clockX;
		int yPieceBlack = 52;
		int countBlack = 0;
		for (Piece p : topPieces) {
			countBlack++;
			Image imgP = facade.getImage(p);
			if (imgP != null) {
				g.drawImage(imgP, xPieceBlack, yPieceBlack, 20, 20, null);
			}
			if (countBlack % 6 == 0) {
				yPieceBlack += 20;
			}
			if (countBlack < 6 && countBlack != 7) {
				xPieceBlack += 15;
			} else if (countBlack > 6 && countBlack != 7 && countBlack < 12) {
				xPieceBlack -= 15;
			} else if (countBlack > 12 && countBlack != 13) {
				xPieceBlack += 15;
			}
			if (countBlack == 7) {
				xPieceBlack -= 15;
			} else if (countBlack == 13) {
				xPieceBlack += 15;
			}
		}
    }

	public void setFacade(Facade facade) {
		this.facade = facade;
	}

    public void setColor(boolean color){
        this.color = color;
    }

	public void setWhiteFallenPieces(List<Piece> whiteFallenPieces) {
        this.whiteFallenPieces = whiteFallenPieces;
    }

    public void setBlackFallenPieces(List<Piece> blackFallenPieces) {
        this.blackFallenPieces = blackFallenPieces;
    }
}
