package de.thm.informatik.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.*;
import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.util.PieceImageLoader;

import java.util.EnumMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

public class SetupPositionPanel extends JPanel {

	private Consumer<Board> onDoneCallback;

	private final int squareSize = 95;
	private boolean color = true;

	private final Board board;
	private Piece selectedPiece = Piece.NONE;

	private JFrame parentFrame;
	private JPanel selectionPanel;

	private JButton doneButton;
	private JButton whitePawnButton;
	private JButton whiteRookButton;
	private JButton whiteBishopButton;
	private JButton whiteKnightButton;
	private JButton whiteQueenButton;
	private JButton whiteKingButton;
	private JButton blackPawnButton;
	private JButton blackRookButton;
	private JButton blackBishopButton;
	private JButton blackKnightButton;
	private JButton blackQueenButton;
	private JButton blackKingButton;

	// Konstruktor
	public SetupPositionPanel(JFrame parentFrame, Consumer<Board> onDoneCallback) {
		this.parentFrame = parentFrame;
		this.onDoneCallback = onDoneCallback;
		setLayout(null);

		this.board = new Board();
		board.clear();

		doneButton = new JButton("Done");

		whitePawnButton = new JButton("WHITE PAWN");
		whiteRookButton = new JButton("WHITE ROOK");
		whiteBishopButton = new JButton("WHITE BISHOP");
		whiteKnightButton = new JButton("WHITE KNIGHT");
		whiteQueenButton = new JButton("WHITE QUEEN");
		whiteKingButton = new JButton("WHITE KING");
		blackPawnButton = new JButton("BLACK PAWN");
		blackRookButton = new JButton("BLACK ROOK");
		blackBishopButton = new JButton("BLACK BISHOP");
		blackKnightButton = new JButton("BLACK KNIGHT");
		blackQueenButton = new JButton("BLACK QUEEN");
		blackKingButton = new JButton("BLACK KING");

		// Buttons zum Panel hinzufügen
		add(doneButton);
		add(whitePawnButton);
		add(whiteRookButton);
		add(whiteBishopButton);
		add(whiteKnightButton);
		add(whiteQueenButton);
		add(whiteKingButton);
		add(blackPawnButton);
		add(blackRookButton);
		add(blackBishopButton);
		add(blackKnightButton);
		add(blackQueenButton);
		add(blackKingButton);

		// Platzierung der Buttons
		int buttonWidth = 140;
		int buttonHeight = 30;
		int spacing = 10;

		int startXWhite = 850;
		int startXBlack = startXWhite + buttonWidth + spacing;
		int startY = 50;

		startXWhite = 800;
		startXBlack = 950;

		whitePawnButton.setBounds(startXWhite, startY + 0 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		whiteRookButton.setBounds(startXWhite, startY + 1 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		whiteKnightButton.setBounds(startXWhite, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		whiteBishopButton.setBounds(startXWhite, startY + 3 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		whiteQueenButton.setBounds(startXWhite, startY + 4 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		whiteKingButton.setBounds(startXWhite, startY + 5 * (buttonHeight + spacing), buttonWidth, buttonHeight);

		blackPawnButton.setBounds(startXBlack, startY + 0 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		blackRookButton.setBounds(startXBlack, startY + 1 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		blackKnightButton.setBounds(startXBlack, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		blackBishopButton.setBounds(startXBlack, startY + 3 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		blackQueenButton.setBounds(startXBlack, startY + 4 * (buttonHeight + spacing), buttonWidth, buttonHeight);
		blackKingButton.setBounds(startXBlack, startY + 5 * (buttonHeight + spacing), buttonWidth, buttonHeight);

		doneButton.setBounds(startXWhite, startY + 7 * (buttonHeight + spacing), buttonWidth * 2 + spacing,
				buttonHeight);

		// Button Logik
		doneButton.addActionListener(e -> {
			if (onDoneCallback != null) {
				onDoneCallback.accept(board);
			}

			if (parentFrame != null) {
				parentFrame.dispose();
			}
		});

		whitePawnButton.addActionListener(e -> selectedPiece = Piece.WHITE_PAWN);
		whiteRookButton.addActionListener(e -> selectedPiece = Piece.WHITE_ROOK);
		whiteBishopButton.addActionListener(e -> selectedPiece = Piece.WHITE_BISHOP);
		whiteKnightButton.addActionListener(e -> selectedPiece = Piece.WHITE_KNIGHT);
		whiteQueenButton.addActionListener(e -> selectedPiece = Piece.WHITE_QUEEN);
		whiteKingButton.addActionListener(e -> selectedPiece = Piece.WHITE_KING);

		blackPawnButton.addActionListener(e -> selectedPiece = Piece.BLACK_PAWN);
		blackRookButton.addActionListener(e -> selectedPiece = Piece.BLACK_ROOK);
		blackBishopButton.addActionListener(e -> selectedPiece = Piece.BLACK_BISHOP);
		blackKnightButton.addActionListener(e -> selectedPiece = Piece.BLACK_KNIGHT);
		blackQueenButton.addActionListener(e -> selectedPiece = Piece.BLACK_QUEEN);
		blackKingButton.addActionListener(e -> selectedPiece = Piece.BLACK_KING);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleBoardClick(e.getX(), e.getY());
			}
		});

	}

	private void handleBoardClick(int mouseX, int mouseY) {
		// Prüfen ob der Klick im Schachbrett ist
		if (mouseX >= 0 && mouseX < squareSize * 8 && mouseY >= 0 && mouseY < squareSize * 8) {
			int file = mouseX / squareSize;
			int rank = 7 - (mouseY / squareSize); // wegen Zeichnungsrichtung

			// Wenn Brett invertiert ist, umdrehen
			if (!color) {
				file = 7 - file;
				rank = mouseY / squareSize;
			}

			// Feld bestimmen
			char fileChar = (char) ('A' + file);
			int rankNum = rank + 1;
			Square sq = Square.valueOf("" + fileChar + rankNum);

			if (selectedPiece != Piece.NONE) {
				board.setPiece(selectedPiece, sq);
				repaint();
			}
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Rechte Bildschirmhälfte dunkelgrün färben
		Graphics2D g2 = (Graphics2D) g.create();
		Color colorRightSide = new Color(180, 180, 180);
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		g2.setColor(colorRightSide);
		g2.fillRect(panelWidth / 2, 0, panelWidth / 2, panelHeight);

		int boardPixelSize = 8 * squareSize;

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

		// Figuren zeichnen
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int drawRank = color ? 7 - rank : rank;
				int drawFile = color ? file : 7 - file;

				Square sq = Square.valueOf("" + (char) ('A' + file) + (rank + 1));
				Piece piece = board.getPiece(sq);
				if (piece != Piece.NONE) {
					Image img = PieceImageLoader.getImage(piece);
					if (img != null) {
						g.drawImage(img, drawFile * squareSize, drawRank * squareSize, squareSize, squareSize, this);
					}
				}
			}
		}

	}

	public Board getCustomBoard() {
		return board;
	}

}
