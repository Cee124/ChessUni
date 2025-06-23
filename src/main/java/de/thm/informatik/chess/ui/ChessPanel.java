package de.thm.informatik.chess.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import static com.github.bhlangonijr.chesslib.Side.WHITE;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.Facade;
import de.thm.informatik.chess.domain.GameState;
import de.thm.informatik.chess.domain.QuickHandler;
import de.thm.informatik.chess.domain.ShowMoveOption;
import de.thm.informatik.chess.service.OpeningDetection;
import de.thm.informatik.chess.service.PGNHandling;
import de.thm.informatik.chess.util.PieceIconLoader;
import de.thm.informatik.chess.util.UciParser;

public class ChessPanel extends JPanel {

	private static final Logger logger = LogManager.getLogger(ChessPanel.class);

	private ChessEngine engine = new ChessEngine();
	
	private final ClockHandler handlerC;
	private SkipHandler handlerS;
	private DrawBoard drawB;
	private final FallenPiecesHandler drawFP ;
	private Board board;
	private QuickHandler quickHandler;
	private final OpeningDetection detector;
	private GameState quickSaveState = null;

	private Square selectedSquare = null;
	private final int squareSize = 95;

	private final List<Move> moveHistory;
	private int currentMoveIndex;

	private final JButton forwardButton;
	private final JButton rewindButton;
	private final JButton startButton;
	private final JButton pauseButton;
	private final JButton quicksaveButton;
	private final JButton quickloadButton;
    private final JButton loadPGNButton;
    private final JButton savePGNButton;

	private final Map<String, String> openingMap;
	private String lastDetectedOpening = "Keine Eröffnung erkannt";

	public boolean rewindSelectedPanel = false;

	public boolean color = true;

	private List<Piece> whiteFallenPieces = new ArrayList<>();
	private List<Piece> blackFallenPieces = new ArrayList<>();

	private ShowMoveOption moveOption;
	private List<Square> highlightedSquares = new ArrayList<>();

	private Facade facade;
	private boolean isCustomBoard = false;

	public ChessPanel(ClockHandler handlerC) throws IOException {

		
		this.drawFP = new FallenPiecesHandler(whiteFallenPieces, blackFallenPieces, squareSize, color, null);
		facade = new Facade(handlerC, whiteFallenPieces, blackFallenPieces, currentMoveIndex, drawFP);
		this.drawFP.setFacade(facade);
		moveHistory = facade.getMoveHistory();
		this.drawB = new DrawBoard(facade, squareSize, color);
		this.handlerC = handlerC;
		handlerC.setPanel(this);
		handlerC.setFacade(facade);
		handlerC.addClock(5);

		handlerS = new SkipHandler(facade.getEngine(), facade);
		handlerS.setPanel(this);
		handlerS.setHandler(handlerC);

		// this.quickHandler = facade.getQuickHandler();
		
		detector = facade.getOpeningDetection();
		openingMap = facade.getOpeningsMap();

		moveOption = facade.getMoveOption();

		//Um Objekte individuell anordnen zu können
		setLayout(null);

		//Icons für Buttons holen
		forwardButton = new JButton(PieceIconLoader.FORWARD_ICON);
		rewindButton = new JButton(PieceIconLoader.REWIND_ICON);
		startButton = new JButton(PieceIconLoader.START_ICON);
		pauseButton = new JButton(PieceIconLoader.PAUSE_ICON);
		quicksaveButton = new JButton(PieceIconLoader.QUICKSAVE_ICON);
		quickloadButton = new JButton(PieceIconLoader.QUICKLOAD_ICON);
		loadPGNButton = new JButton("PGN laden");
		savePGNButton = new JButton("PGN speichern");

		//Buttons dem Panel hinzufügen
		add(forwardButton);
		add(rewindButton);
		add(startButton);
		add(pauseButton);
		add(quicksaveButton);
		add(quickloadButton);
		add(loadPGNButton);
		add(savePGNButton);

		//Button Logik
		forwardButton.addActionListener(_ -> handlerS.fastForwardMove());
		rewindButton.addActionListener(_ -> handlerS.rewindMove());
		startButton.addActionListener(_ -> handlerC.startClocks());
		pauseButton.addActionListener(_ -> handlerC.pauseClocks());

		quicksaveButton.addActionListener(_ -> {
			facade.quicksave();
		});

		quickloadButton.addActionListener(_ -> {
			facade.quickload();
		});
		
        loadPGNButton.addActionListener(_ -> {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("PGN-Datei laden");

            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File fileToLoad = fileChooser.getSelectedFile();
                String filePath = fileToLoad.getAbsolutePath();
                
                PGNHandling.loadGame(filePath, facade);
                currentMoveIndex = moveHistory.size();
                handlerC.pauseClocks();           
                handlerC.setWhiteRemaining(0);    
                handlerC.setBlackRemaining(0);

                repaint();
            }
        });

        savePGNButton.addActionListener(_ -> {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        	String timestamp = LocalDateTime.now().format(formatter);
        	String filePath = "games/game_" + timestamp + ".pgn";
        	
            PGNHandling pgnHandler = new PGNHandling(filePath, facade);
            pgnHandler.saveGame(facade.getMoveHistory());
        });

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int file = color ? e.getX() / squareSize : 7 - (e.getX() / squareSize);
				int rank = color ? 7 - (e.getY() / squareSize) : e.getY() / squareSize;
				Square clickedSquare = drawB.squareFromCoords(rank, file);

				Piece targetPiece = facade.getPiece(clickedSquare);
				boolean isCaptured = targetPiece != Piece.NONE;

				if (selectedSquare == null) {
					if (facade.getPiece(clickedSquare) != Piece.NONE) {
						selectedSquare = clickedSquare;
						highlightedSquares = facade.getLegalTargetSquares(selectedSquare);
						repaint();
					}
				} else {
					Move move = new Move(selectedSquare, clickedSquare);
					// Liste aller legalen Moves
					List<Move> legalMoves = facade.getLegalMoves();

					// Wenn die Liste eine Zug enthält
					if (legalMoves.contains(move)) {
						Side movingSide = facade.getBoard().getSideToMove();
						if (isCaptured) {
							if (facade.getBoard().getSideToMove() == WHITE) {
								whiteFallenPieces.add(targetPiece);
							} else {
								blackFallenPieces.add(targetPiece);
							}
						}
						// Zug wird ausgeführt
						facade.makeMove(move);
						if (currentMoveIndex != moveHistory.size()) {
        					moveHistory.subList(currentMoveIndex, moveHistory.size()).clear();
    					}
						moveHistory.add(move);
						currentMoveIndex = moveHistory.size();

						Side nextSide = facade.getBoard().getSideToMove();
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

						//Schachmatt-Erkennung
						if (facade.isCheckmate()) {
							JOptionPane.showMessageDialog(ChessPanel.this,
									"Checkmate! " + (movingSide == WHITE ? "Black" : "White") + " loses.");
							handlerC.pauseClocks();
						}
						//Schach-Erkennung
						else if (facade.isInCheck()) {
							JOptionPane.showMessageDialog(ChessPanel.this,
									(nextSide == WHITE ? "White" : "Black") + " is in Check!");
						} else if (facade.isGameOver()) {
							JOptionPane.showMessageDialog(ChessPanel.this, "The game is over");
						}
						//Aktualisierung der Ansicht
						repaint();
					} else {
						logger.error("Illegal Move: {}", move);
					}
					selectedSquare = null;
					highlightedSquares.clear();
					repaint();
				}
			}
		});
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
		loadPGNButton.setBounds(1250, 500, 150, 30);
        savePGNButton.setBounds(1250, 540, 150, 30);
	}

	@Override
	// Methode zum Formatieren und Bearbeiten von Objekten
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawB.drawBoard(g, highlightedSquares);
		drawFP.drawFallenPieces(g);

		List<Move> moveHistory = facade.getMoveHistory();

		//Rechte Bildschirmhälfte dunkelgrün färben
		Graphics2D g2 = (Graphics2D) g.create();
		Color colorRightSide = new Color(180, 180, 180);
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		g2.setColor(colorRightSide);
		g2.fillRect(panelWidth / 2, 0, panelWidth / 2, panelHeight);

		int boardPixelSize = 8 * squareSize;

		//General specs für Stats Window
		int statsX = getWidth() - 300 - 100;
		int statsY = 200;

		//Opening Detection Window
		int openingRectY = statsY - 40;
		int openingRectWidth = 300;
		int openingRectHeight = 40;

		//Rechteck schwarz und dicke 3 und dann Zeichnen mit specs
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(statsX, openingRectY, openingRectWidth, openingRectHeight);

		//Zeichnen der Eröffnungserkennung
		drawOpeningDetection(g2, statsX);

		//Zeichnen der Uhren
		int clockX = boardPixelSize + 50;
		drawClocks(g2, clockX, boardPixelSize);

		//Zeichnen des StatsWindow
		drawStatsWindow(g2, moveHistory, statsX, statsY);
		
		g2.dispose();
	}

	private String getLastDetectedOpening(String sanAnnotated){
		//Durch Map mit Eröffnungen iterieren
		for (Map.Entry<String, String> entry : openingMap.entrySet()) {
			//Wenn aktuelle Zugabfolge mit Opening übereinstimmt dann break und der
			//openingText wird auf den openingName gesetzt
			if (sanAnnotated.equals(entry.getKey())) {
				//Damit falls nichts mehr erkannt wird die letzte Eröffnung gespeichert wird
				lastDetectedOpening = entry.getValue();
				return entry.getValue();
			}
		}
		return lastDetectedOpening;
	}

	public void drawOpeningDetection(Graphics g2, int statsX){
		int openingRectY = 160; // statsY - 40 (statsY = 200)
    	int openingRectWidth = 300;
    	int openingRectHeight = 40;

    	g2.setColor(Color.BLACK);
    	((Graphics2D) g2).setStroke(new BasicStroke(3));
    	g2.drawRect(statsX, openingRectY, openingRectWidth, openingRectHeight);

    	String openingText = lastDetectedOpening;
    	if (!isCustomBoard) {
        	String currentUciMoves = convertMoveListToUci(facade.getMoveHistory());
        	String sanAnnotated = UciParser.convertUciToAnnotatedMoves(currentUciMoves);
        	openingText = getLastDetectedOpening(sanAnnotated);
    	}

    	g2.setColor(openingText.equals("Keine Eröffnung erkannt") ? new Color(220, 20, 60): new Color(60, 179, 13));
    	g2.setFont(new Font("SansSerif", Font.BOLD, 14));
    	FontMetrics fm = g2.getFontMetrics();
    	int textWidth = fm.stringWidth(openingText);
    	g2.drawString(openingText, statsX + (openingRectWidth - textWidth) / 2, openingRectY + 25);
	}

	public void drawClocks(Graphics g2, int clockX, int boardPixelSize){
		// Draw clocks
		g2.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

		//White clock
		//Wenn weiße Uhr läuft dann rote Darstellung sonst schwarz
		g2.setColor(handlerC.isWhiteRunning() ? Color.RED : Color.BLACK);
		// Weße Uhr zeichnen
		g2.drawString(formatTime(handlerC.getWhiteRemaining()), clockX, boardPixelSize - 50);

		//Black clock
		//Wenn schwarze Uhr läuft dann rote Darstellung sonst schwarz
		g2.setColor(handlerC.isBlackRunning() ? Color.RED : Color.BLACK);
		//Schwarze Uhr zeichnen
		g2.drawString(formatTime(handlerC.getBlackRemaining()), clockX, 50);
	}

	public void drawStatsWindow(Graphics g2, List<Move> moveHistory, int statsX, int statsY){
		//Wenn züge < 10 dann zuganzahl und sonst 10
		int visibleMoves = Math.min(10, moveHistory.size());
		//mind. 40 und maximal 220(da visibleMoves max 10)
		int statsRectHeight = Math.max(40, visibleMoves * 20);

		g2.setColor(Color.BLACK);
		((Graphics2D) g2).setStroke(new BasicStroke(3));
		g2.drawRect(statsX, statsY, 300, 40);
		g2.drawRect(statsX, statsY + 40, 300, statsRectHeight);

		// Wenn mehr als 10 Züge (0, 12-10 = 0, 2 -> index 2 bis 11) sonst 0 bis
		// move.size()
		// also wird hier der Startindex ermittelt
		int start = Math.max(0, moveHistory.size() - 10);
		for (int i = start; i < moveHistory.size(); i++) {
			//Liefert boolean zurück ob gerade
			Predicate<Integer> isWhite = p -> p % 2 == 0;
			//Wenn gerade dann zug weiß sonst schwarz
			String colorText = isWhite.test(i) ? "Weiß" : "Schwarz";
			//aktueller move als text in variable speichern
			String moveText = moveHistory.get(i).toString();

			//Y Position wird dynamisch ermittelt je nachdem wie vielter zug es ist
			int offset = i - start;
			int textY = statsY + 55 + offset * 20;

			//Wenn i gerade dann ist die Schriftfarbe weiß sonst schwarz
			g2.setFont(new Font("Monospaced", Font.BOLD, 14));
			g2.setColor(isWhite.test(i) ? Color.WHITE : Color.BLACK);

			//einzelteile zusammensetzen
			String label = colorText + ":";
			FontMetrics fm1 = g2.getFontMetrics();
			int labelWidth = fm1.stringWidth(label);

			//label und text zeichnen
			g2.drawString(label, statsX + 100, textY);
			g2.drawString(moveText, statsX + 100 + labelWidth + 10, textY);

			//Trennlinie erweiter sich dynamisch
			g2.setColor(Color.BLACK);
			g2.drawLine(statsX, statsY + 60 + offset * 20, statsX + 300, statsY + 60 + offset * 20);
		}
	}


	//Methode um Zeitanzeige im format mm:ss zu erstellen
	private String formatTime(long millis) {
		long totalSeconds = millis / 1000;
		long minutes = totalSeconds / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	//Methode um Spielfarbe festzulegen
	public void setColor(boolean isWhite) {
		this.color = isWhite;
		this.drawB.setColor(color);
		this.drawFP.setColor(color);
		handlerC.setColor(isWhite);
		repaint();
	}

	//Methode um aktuelle züge in Uci Format darzustellen
	private String convertMoveListToUci(List<Move> moves) {
		StringBuilder sb = new StringBuilder();
		for (Move move : moves) {
			sb.append(move.toString());
		}
		return sb.toString();
	}
	
	public void setCurrentMoveIndex(int currentMoveIndex) {
		this.currentMoveIndex = currentMoveIndex;
	}

	public void setCustomBoard(Board customBoard) {
		facade.setBoard(customBoard);
		setBoard(customBoard);
		isCustomBoard = true;
	    repaint();
	}

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
    
	public ChessEngine getEngine() {
		return engine;
	}

	public void setRewind(boolean enableRewind) {
		this.rewindSelectedPanel = enableRewind;
	}

	public int getCurrentMoveIndex(){
		return currentMoveIndex;
	}

	public DrawBoard getDrawBoard(){
		return drawB;
	}

	public Facade getFacade() {
		return facade;
	}
}