package de.thm.informatik.chess.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import static com.github.bhlangonijr.chesslib.Side.WHITE;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.ClockHandler;
import de.thm.informatik.chess.domain.GameState;
import de.thm.informatik.chess.domain.OpeningDetection;
import de.thm.informatik.chess.domain.ShowMoveOption;
import de.thm.informatik.chess.domain.SkipHandler;
import de.thm.informatik.chess.domain.UciParser;
import de.thm.informatik.chess.util.PieceImageLoader;

public class ChessPanel extends JPanel {

	private ChessEngine engine = new ChessEngine();
	private ClockHandler handlerC;
	private SkipHandler handlerS;

	private Square selectedSquare = null;
	private final int squareSize = 95;
	private static final List<Move> moveHistory = new LinkedList<>();

	private final JButton forwardButton;
	private final JButton rewindButton;
	private final JButton startButton;
	private final JButton pauseButton;
	private final JButton quicksaveButton;
	private final JButton quickloadButton;

	private int currentMoveIndex;

	private final OpeningDetection detector;
	private final Map<String, String> openingMap;

	private String lastDetectedOpening = "Keine Eröffnung erkannt";

	private boolean rewindSelectedPanel = false;
	public boolean color = true;

	private GameState quickSaveState = null;

	private static final Logger logger = LogManager.getLogger(ChessPanel.class);

	private List<Piece> whiteFallenPieces = new ArrayList<>();
	private List<Piece> blackFallenPieces = new ArrayList<>();

	private ShowMoveOption moveOption;
	private List<Square> highlightedSquares = new ArrayList<>();
	private boolean showMoveOptionsSelectedPanel = false;

	public void setShowMoveOptions(boolean enableShowMoveOptions) {
		this.showMoveOptionsSelectedPanel = enableShowMoveOptions;
	}

	public void setRewind(boolean enableRewind) {
		this.rewindSelectedPanel = enableRewind;
	}

	public int getCurrentMoveIndex(){
		return currentMoveIndex;
	}

	public void setCurrentMoveIndex(int index){
		this.currentMoveIndex = index;
	}

	public ChessPanel(ClockHandler handlerC) throws IOException {
		this.handlerC = handlerC;
		handlerC.setPanel(this);
		handlerC.setEngine(engine);
		handlerC.addClock(5);

		handlerS = new SkipHandler(engine);
		handlerS.setPanel(this);
		handlerS.setHandler(handlerC);

		detector = new OpeningDetection();

		openingMap = detector.loadOpenings("/Openings/eco_openings.html");

		moveOption = new ShowMoveOption(engine);

		// Um Objekte individuell anordnen zu können
		setLayout(null);

		// Icons für Buttons holen
		forwardButton = new JButton(IconLoader.FORWARD_ICON);
		rewindButton = new JButton(IconLoader.REWIND_ICON);
		startButton = new JButton(IconLoader.START_ICON);
		pauseButton = new JButton(IconLoader.PAUSE_ICON);
		quicksaveButton = new JButton(IconLoader.QUICKSAVE_ICON);
		quickloadButton = new JButton(IconLoader.QUICKLOAD_ICON);

		// Buttons dem Panel hinzufügen
		add(forwardButton);
		add(rewindButton);
		add(startButton);
		add(pauseButton);
		add(quicksaveButton);
		add(quickloadButton);

		//Button Logik
		forwardButton.addActionListener(e -> handlerS.fastForwardMove());
		rewindButton.addActionListener(e -> handlerS.rewindMove());
		startButton.addActionListener(e -> handlerC.startClocks());
		pauseButton.addActionListener(e -> handlerC.pauseClocks());

		quicksaveButton.addActionListener(e -> {
			quicksave();
		});

		quickloadButton.addActionListener(e -> {
			quickload();
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int file = color ? e.getX() / squareSize : 7 - (e.getX() / squareSize);
				int rank = color ? 7 - (e.getY() / squareSize) : e.getY() / squareSize;
				Square clickedSquare = squareFromCoords(rank, file);

				Piece targetPiece = engine.getPiece(clickedSquare);
				boolean isCaptured = targetPiece != Piece.NONE;

				if (selectedSquare == null) {
					if (engine.getPiece(clickedSquare) != Piece.NONE) {
						selectedSquare = clickedSquare;
						highlightedSquares = moveOption.getLegalTargetSquares(selectedSquare);
						repaint();
					}
				} else {
					Move move = new Move(selectedSquare, clickedSquare);
					// Liste aller legalen Moves
					List<Move> legalMoves = engine.getLegalMoves();

					// Wenn die Liste eine Zug enthält
					if (legalMoves.contains(move)) {
						Side movingSide = engine.getBoard().getSideToMove();
						if (isCaptured) {
							if (engine.getBoard().getSideToMove() == WHITE) {
								whiteFallenPieces.add(targetPiece);
							} else {
								blackFallenPieces.add(targetPiece);
							}
						}
						// Zug wird ausgeführt
						engine.makeMove(move);
						if (currentMoveIndex != moveHistory.size()) {
        					moveHistory.subList(currentMoveIndex, moveHistory.size()).clear();
    					}
						moveHistory.add(move);
						currentMoveIndex = moveHistory.size();

						Side nextSide = engine.getBoard().getSideToMove();
						if (nextSide == WHITE) {
							if (color) {
								handlerC.startWhiteClock();
							} else {
								handlerC.startBlackClock();
							}
						} else {
							if (color) {
								handlerC.startBlackClock();
							} else {
								handlerC.startWhiteClock();
							}
						}

						// Schachmatt-Erkennung
						if (engine.isCheckmate()) {
							JOptionPane.showMessageDialog(ChessPanel.this,
									"Checkmate! " + (movingSide == WHITE ? "Black" : "White") + " loses.");
							handlerC.pauseClocks();
						}
						// Schach-Erkennung
						else if (engine.isInCheck()) {
							JOptionPane.showMessageDialog(ChessPanel.this,
									(nextSide == WHITE ? "Schwarz" : "Weiß") + " is in Check!");
						} else if (engine.isGameOver()) {
							JOptionPane.showMessageDialog(ChessPanel.this, "The game is over");
						}
						// Aktualisierung der Ansicht
						repaint();
						// Wenn kein legaler Zug erkannt wurde Fehlermeldung ausgeben
					} else {
						logger.debug("Illegal Move: " + move);
					}
					selectedSquare = null;
					highlightedSquares.clear();
					// Ansicht akutalisieren
					repaint();
				}
			}
		});
	}

	// Methode um Liste gemachter Züge zurückzugeben
	public static List<Move> getMoveHistory() {
		return moveHistory;
	}

	@Override
	// Methode zum festlegen der Button Positionen
	public void doLayout() {
		super.doLayout();

		int statsX = getWidth() - 300 - 100;
		int statsY = 200;

		int buttonY = statsY + 5;
		int rewindButtonX = statsX + 10;
		int forwardButtonX = statsX + 260;
		int operationRectWidth = 300;
		int buttonsTotalWidth = 30 + 20 + 30;
		int centerInStats = statsX + (operationRectWidth - buttonsTotalWidth) / 2;

		pauseButton.setBounds(centerInStats, buttonY, 30, 30);
		startButton.setBounds(centerInStats + 30 + 20, buttonY, 30, 30);
		rewindButton.setBounds(rewindButtonX, buttonY, 30, 30);
		forwardButton.setBounds(forwardButtonX, buttonY, 30, 30);
		quicksaveButton.setBounds(centerInStats - 30 - 20, buttonY, 30, 30);
		quickloadButton.setBounds(centerInStats + 60 + 40, buttonY, 30, 30);

	}

	@Override
	// Methode zum Formatieren und Bearbeiten von Objekten
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

		// Specs für Uhren Positionen
		int clockX = boardPixelSize + 50;
		int whiteClockY = boardPixelSize - 50;
		int blackClockY = 50;

		// General specs für Stats Window
		int statsX = getWidth() - 300 - 100;
		int statsY = 200;

		// Opening Detection Window
		int openingRectX = statsX;
		int openingRectY = statsY - 40;
		int openingRectWidth = 300;
		int openingRectHeight = 40;

		// Rechteck schwarz und dicke 3 und dann Zeichnen mit specs
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(openingRectX, openingRectY, openingRectWidth, openingRectHeight);

		// Initialisierung um Openings darstellen zu können
		List<Move> currentMoves = getMoveHistory();
		String currentUciMoves = convertMoveListToUci(currentMoves);
		String sanAnnotated = UciParser.convertUciToAnnotatedMoves(currentUciMoves);
		// Variablenzuweisung um letzte erkannte Eröffnung zu speichern
		String openingText = lastDetectedOpening;

		// Durch Map mit Eröffnungen iterieren
		for (Map.Entry<String, String> entry : openingMap.entrySet()) {
			// Key und Value der Map in extra Variablen speichern
			String openingSequence = entry.getKey();
			String openingName = entry.getValue();

			// Wenn aktuelle Zugabfolge mit Opening übereinstimmt dann break und der
			// openingText wird auf den openingName gesetzt
			if (sanAnnotated.equals(openingSequence)) {
				// Damit falls nichts mehr erkannt wird die letzte Eröffnung gespeichert wird
				lastDetectedOpening = openingName;
				openingText = openingName;
				break;
			}
		}

		// Schrift für OpeningText
		if (!openingText.equals("Keine Eröffnung erkannt")) {
			g2.setColor(new Color(60, 179, 13));
		} else {
			g2.setColor(new Color(220, 20, 60));
		}
		g2.setFont(new Font("SansSerif", Font.BOLD, 14));
		FontMetrics fm = g2.getFontMetrics();
		// Ermittelt breite von openingText
		int textWidth = fm.stringWidth(openingText);
		// Opening Text schreiben
		g2.drawString(openingText, openingRectX + (openingRectWidth - textWidth) / 2, openingRectY + 25);

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
						g.drawImage(img, drawFile * squareSize, drawRank * squareSize, squareSize, squareSize, this);
					}
				}
			}
		}

		// Specs fallen pieces
		List<Piece> bottomPieces = color ? whiteFallenPieces : blackFallenPieces;
		List<Piece> topPieces = color ? blackFallenPieces : whiteFallenPieces;

		// Draw fallen pieces white
		int xPieceWhite = clockX;
		int yPieceWhite = boardPixelSize - 100;
		int countWhite = 0;
		for (Piece p : bottomPieces) {
			countWhite++;
			Image imgP = PieceImageLoader.getImage(p);
			if (imgP != null) {
				g.drawImage(imgP, xPieceWhite, yPieceWhite, 20, 20, this);
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
			Image imgP = PieceImageLoader.getImage(p);
			if (imgP != null) {
				g.drawImage(imgP, xPieceBlack, yPieceBlack, 20, 20, this);
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

		// Draw clocks
		g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

		//White clock
		//Wenn weiße Uhr läuft dann rote Darstellung sonst schwarz
		g.setColor(handlerC.isWhiteRunning() ? Color.RED : Color.BLACK);
		//Da in ms dargestellt muss man durch 1000 teilen für Sekunden
		long whiteTimeMs = handlerC.getWhiteRemaining();
		String whiteTime = formatTime(whiteTimeMs);
		// Weße Uhr zeichnen
		g.drawString(whiteTime, clockX, whiteClockY);

		//Black clock
		//Wenn schwarze Uhr läuft dann rote Darstellung sonst schwarz
		g.setColor(handlerC.isBlackRunning() ? Color.RED : Color.BLACK);
		//Da in ms dargestellt muss man durch 1000 teilen für Sekunden
		long blackTimeMs = handlerC.getBlackRemaining();
		String blackTime = formatTime(blackTimeMs);
		// Schwarze Uhr zeichnen
		g.drawString(blackTime, clockX, blackClockY);

		// Draw stats display
		g.setFont(new Font("Monospaced", Font.PLAIN, 14));
		g.setColor(Color.BLACK);

		List<Move> moves = getMoveHistory();

		int operationRectY = statsY;
		int operationRectHeight = 40;

		int statsRectY = statsY + 40;
		int statsRectWidth = 300;
		// Wenn züge < 10 dann zuganzahl und sonst 10
		int visibleMoves = Math.min(10, moves.size());
		// mind. 40 und maximal 220(da visibleMoves max 10)
		int statsRectHeight = Math.max(40, visibleMoves * 20);

		int operationRectWidth = 300;

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(statsX, operationRectY, operationRectWidth, operationRectHeight);
		g2.drawRect(statsX, statsRectY, statsRectWidth, statsRectHeight);

		// Wenn mehr als 10 Züge (0, 12-10 = 0, 2 -> index 2 bis 11) sonst 0 bis
		// move.size()
		// also wird hier der Startindex ermittelt
		int start = Math.max(0, moves.size() - 10);
		for (int i = start; i < moves.size(); i++) {
			// Liefert boolean zurück ob gerade
			Predicate<Integer> isWhite = p -> p % 2 == 0;
			// Wenn gerade dann zug weiß sonst schwarz
			String colorText = isWhite.test(i) ? "Weiß" : "Schwarz";
			// aktueller move als text in variable speichern
			String moveText = moves.get(i).toString();

			// Y Position wird dynamisch ermittelt je nachdem wie vielter zug es ist
			int offset = i - start;
			int textY = statsY + 55 + offset * 20;

			// Wenn i gerade dann ist die Schriftfarbe weiß sonst schwarz
			g2.setFont(new Font("Monospaced", Font.BOLD, 14));
			g2.setColor(isWhite.test(i) ? Color.WHITE : Color.BLACK);

			// einzelteile zusammensetzen
			String label = colorText + ":";
			FontMetrics fm1 = g2.getFontMetrics();
			int labelWidth = fm1.stringWidth(label);

			// label und text zeichnen
			g2.drawString(label, statsX + 100, textY);
			g2.drawString(moveText, statsX + 100 + labelWidth + 10, textY);

			// Trennlinie erweiter sich dynamisch
			g2.setColor(Color.BLACK);
			g2.drawLine(statsX, statsY + 60 + offset * 20, statsX + 300, statsY + 60 + offset * 20);
		}
		g2.dispose();

	}

	// Methode um Zeitanzeige im format mm:ss zu erstellen
	private String formatTime(long millis) {
		long totalSeconds = millis / 1000;
		long minutes = totalSeconds / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	// Methode um Spielfarbe festzulegen
	public void setColor(boolean isWhite) {
		this.color = isWhite;
		handlerC.setColor(isWhite);
		repaint();
	}

	// Methode um aktuelle züge in Uci Format darzustellen
	private String convertMoveListToUci(List<Move> moves) {
		StringBuilder sb = new StringBuilder();
		for (Move move : moves) {
			sb.append(move.toString()); // Check if ok
		}
		return sb.toString();
	}

	private Square squareFromCoords(int rank, int file) {
		char fileChar = (char) ('A' + file);
		char rankChar = (char) ('1' + rank);
		String squareName = "" + fileChar + rankChar;
		return Square.valueOf(squareName);
	}

	public void quicksave() {
		quickSaveState = new GameState(engine.getBoard().clone(), currentMoveIndex, handlerC.getWhiteRemaining(),
				handlerC.getBlackRemaining(), engine.getBoard().getSideToMove());
		logger.info("Quicksave durchgefuehrt.");
	}

	public void quickload() {
		if (quickSaveState == null) {
			logger.warn("Kein Quicksave-Zustand vorhanden.");
			return;
		}

		// Engine zurücksetzen
		engine.reset();

		// Züge bis zum Savepoint erneut ausführen
		currentMoveIndex = quickSaveState.getMoveIndex();
		for (int i = 0; i < currentMoveIndex; i++) {
			engine.makeMove(moveHistory.get(i));
		}

		// SideToMove setzen
		engine.getBoard().setSideToMove(quickSaveState.getSideToMove());

		//Uhren zurücksetzen
		handlerC.setWhiteRemaining(quickSaveState.getWhiteTime());
		handlerC.setBlackRemaining(quickSaveState.getBlackTime());
		
		handlerC.startClocks();

		repaint();

		logger.info("Quickload durchgefuehrt.");
	}

}
