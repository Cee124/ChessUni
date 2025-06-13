package de.thm.informatik.chess.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;

import static com.github.bhlangonijr.chesslib.Side.WHITE;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.OpeningDetection;
import de.thm.informatik.chess.domain.UciParser;

public class ChessPanel extends JPanel {

    private Timer whiteTimer;
    private Timer blackTimer;
    private boolean whiteRunning = false;
    private boolean blackRunning = false;
    private long whiteRemaining;
    private long blackRemaining;
    private ChessEngine engine = new ChessEngine();
    private Square selectedSquare = null;
    private final int squareSize = 95;
    private static final LinkedList<Move> moveHistory = new LinkedList<>();

    private final JButton forwardButton;
    private final JButton rewindButton;
    private final JButton startButton;
    private final JButton pauseButton;

    private final JButton whiteKing;
    private final JButton blackKing;

    private int currentMoveIndex;

    private final OpeningDetection detector;
    private final Map<String, String> openingMap;

    private String lastDetectedOpening = "Keine Eröffnung erkannt";

    private boolean rewindSelectedPanel = false;
    private boolean color = true;

    public void setRewind(boolean enableRewind){
        this.rewindSelectedPanel = enableRewind;
        System.out.println("Rewind gesetzt auf: " + enableRewind);
    }
    
    public ChessPanel() throws IOException {
        detector = new OpeningDetection();

        openingMap = detector.loadOpenings("/Openings/eco_openings.html");

        //Um Objekte individuell anordnen zu können
        setLayout(null);

        //Icons für Buttons holen
        forwardButton = new JButton(IconLoader.FORWARD_ICON);
        rewindButton = new JButton(IconLoader.REWIND_ICON);
        startButton = new JButton(IconLoader.START_ICON);
        pauseButton = new JButton(IconLoader.PAUSE_ICON);
        whiteKing = new JButton(IconLoader.WHITEKING_ICON);
        blackKing = new JButton(IconLoader.BLACKKING_ICON);
        
        //Buttons dem Panel hinzufügen
        add(forwardButton);
        add(rewindButton);
        add(startButton);
        add(pauseButton);
        add(whiteKing);
        add(blackKing);

        //Button Logik
        forwardButton.addActionListener(e -> fastForwardMove());
        rewindButton.addActionListener(e -> rewindMove());
        startButton.addActionListener(e -> startClock());
        pauseButton.addActionListener(e -> pauseClocks());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int file = color ? e.getX() / squareSize : 7 - (e.getX() / squareSize);
                int rank = color ? 7 - (e.getY() / squareSize) : e.getY() / squareSize;
                Square clickedSquare = squareFromCoords(rank, file);

                if (selectedSquare == null) {
                    if (engine.getPiece(clickedSquare) != Piece.NONE) {
                        selectedSquare = clickedSquare;
                        repaint();
                    }
                } else {
                    Move move = new Move(selectedSquare, clickedSquare);
                    //Liste aller legalen Moves
                    List<Move> legalMoves = engine.getLegalMoves();

                    Side currentSide = engine.getBoard().getSideToMove(); // Wer ist JETZT am Zug (vor dem Move)
                    //Wenn die Liste eine Zug enthält
                    if (legalMoves.contains(move)) {
                        //Zug wird ausgeführt
                        engine.makeMove(move);
                        moveHistory.subList(currentMoveIndex, moveHistory.size()).clear();
                        moveHistory.add(move);
                        currentMoveIndex = moveHistory.size();

                        //Wenn weiß am Zug ist, weiße Uhr starten
                        if(currentSide == WHITE){
                            startWhiteClock();
                        //Sonst schwarze Uhr starten
                        }else{
                            startBlackClock();
                        }
                        //Aktualisierung der Ansicht
                        repaint();
                    //Wenn kein legaler Zug erkannt wurde Fehlermeldung ausgeben
                    } else {
                        System.out.println("Illegal move: " + move);
                    }
                    selectedSquare = null;
                    //Ansicht akutalisieren
                    repaint();
                }
            }
        });
    }

    //Methode um Liste gemachter Züge zurückzugeben
    public static LinkedList<Move> getMoveHistory() {
        return moveHistory;
    }

    //Methode für rewind-Button Logik
    private void rewindMove(){
        if(!rewindSelectedPanel){
            return;
        }
        //Wenn Züge gemacht wurden
        if(currentMoveIndex > 0){
            //index auf moveHistory.size() - 1 setzen
            currentMoveIndex--;
            //Schachgame zurücksetzen
            engine.reset();
            //Alle Züge machen die in der moveHistory gespeichert sind bis zu index(moveHistory.size() - 1)
            for(int i = 0; i < currentMoveIndex; i++){
                engine.makeMove(moveHistory.get(i));
            }
            //Ansicht aktualisieren
            repaint();  
        }
    }

    //Methode für forward-Button Logik
    private void fastForwardMove(){
        //Wenn index größer/gleich zuganzahl dann wird nichts gemacht
        if(currentMoveIndex >= moveHistory.size()){
            return;
        }
        //Zug aus der Zugliste holen
        Move forwardMove = moveHistory.get(currentMoveIndex);
        //Rückgängig gemachten Zug ausführen
        engine.makeMove(forwardMove);
        //Index wieder hochzählen
        currentMoveIndex++;
        //Ansicht aktualisieren
        repaint();
    }

    @Override
    //Methode zum festlegen der Button Positionen
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
        whiteKing.setBounds(centerInStats + 60 + 40, buttonY, 30, 30);
        blackKing.setBounds(centerInStats - 30 - 20, buttonY, 30, 30);
    }


    @Override
    //Methode zum Formatieren und Bearbeiten von Objekten
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Rechte Bildschirmhälfte dunkelgrün färben
        Graphics2D g2 = (Graphics2D) g.create();
        Color colorRightSide = new Color(180, 180, 180);
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g2.setColor(colorRightSide);
        g2.fillRect(panelWidth / 2, 0, panelWidth / 2, panelHeight);

        int boardPixelSize = 8 * squareSize;
        
        //Specs für Uhren Positionen
        int clockX = boardPixelSize + 50;
        int whiteClockY = boardPixelSize - 50;
        int blackClockY = 50;                   
        
        //General specs für Stats Window
        int statsX = getWidth() - 300 - 100;
        int statsY = 200;

        //Opening Detection Window
        int openingRectX = statsX;
        int openingRectY = statsY - 100;
        int openingRectWidth = 300;
        int openingRectHeight = 40;

        //Rechteck schwarz und dicke 3 und dann Zeichnen mit specs
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(openingRectX, openingRectY, openingRectWidth, openingRectHeight);

        //Initialisierung um Openings darstellen zu können
        List<Move> currentMoves = getMoveHistory();
        String currentUciMoves = convertMoveListToUci(currentMoves);
        String sanAnnotated = UciParser.convertUciToAnnotatedMoves(currentUciMoves);
        //Variablenzuweisung um letzte erkannte Eröffnung zu speichern
        String openingText = lastDetectedOpening;

        //Durch Map mit Eröffnungen iterieren
        for(Map.Entry<String, String> entry : openingMap.entrySet()){
            //Key und Value der Map in extra Variablen speichern
            String openingSequence = entry.getKey();
            String openingName = entry.getValue();

            //Wenn aktuelle Zugabfolge mit Opening übereinstimmt dann break und der openingText wird auf den openingName gesetzt
            if(sanAnnotated.equals(openingSequence)){
                //Damit falls nichts mehr erkannt wird die letzte Eröffnung gespeichert wird
                lastDetectedOpening = openingName;
                openingText = openingName;
                break;
            }
        }
        
        //Schrift für OpeningText 
        g2.setColor(Color.RED);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        //Ermittelt breite von openingText
        int textWidth = fm.stringWidth(openingText);
        //Opening Text schreiben
        g2.drawString(openingText, openingRectX + (openingRectWidth - textWidth) / 2, openingRectY + 25);

        //Draw chess board
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if ((rank + file) % 2 == 0) {
                    g.setColor(Color.lightGray);
                } else {
                    g.setColor(Color.white);
                }
                g.fillRect(file * squareSize, (7 - rank) * squareSize, squareSize, squareSize);
            }
        }

        //Draw pieces
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int drawRank = color ? 7 - rank : rank;
                int drawFile = color ? file : 7 - file;
        
                //Brettfeld
                if ((rank + file) % 2 == 0) {
                    g.setColor(Color.lightGray);
                } else {
                    g.setColor(Color.white);
                }
                g.fillRect(drawFile * squareSize, drawRank * squareSize, squareSize, squareSize);

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

        //Draw clocks
        g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

        //White clock
        //Wenn weiße Uhr läuft dann rote Darstellung sonst schwarz
        g.setColor(whiteRunning ? Color.RED : Color.BLACK);
        //Da in ms dargestellt muss man durch 1000 teilen für Sekunden
        long whiteSumSeconds = whiteRemaining / 1000;
        //von sekunden in minuten / 60
        long whiteSumMinutes = whiteSumSeconds / 60;
        //Anzahl Sekunden in Abhängigkeit von Minuten
        long whiteSeconds = whiteSumSeconds % 60;
        //Darstellung der Uhr festlegen
        String whiteTime = String.format("%02d:%02d", whiteSumMinutes, whiteSeconds);
        //Weße Uhr zeichnen
        g.drawString(whiteTime, clockX, whiteClockY);

        //Black clock
        //Wenn schwarze Uhr läuft dann rote Darstellung sonst schwarz
        g.setColor(blackRunning ? Color.RED : Color.BLACK);
        //Da in ms dargestellt muss man durch 1000 teilen für Sekunden
        long blackSumSeconds = blackRemaining / 1000;
        //von sekunden in minuten / 60
        long blackSumMinutes = blackSumSeconds / 60;
        //Anzahl Sekunden in Abhängigkeit von Minuten
        long blackSeconds = blackSumSeconds % 60;
        //Darstellung der Uhr festlegen
        String blackTime = String.format("%02d:%02d", blackSumMinutes, blackSeconds);
        //Schwarze Uhr zeichnen
        g.drawString(blackTime, clockX, blackClockY);
    
        //Draw stats display
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.setColor(Color.BLACK);

        List<Move> moves = getMoveHistory();

        int operationRectY = statsY;
        int operationRectHeight = 40;

        int statsRectY = statsY + 40;  
        int statsRectWidth = 300;
        //Wenn züge < 10 dann zuganzahl und sonst 10
        int visibleMoves = Math.min(10, moves.size());
        //mind. 40 und maximal 220(da visibleMoves max 10)
        int statsRectHeight = Math.max(40, visibleMoves * 20);

        int operationRectWidth = 300;

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(statsX, operationRectY, operationRectWidth, operationRectHeight);
        g2.drawRect(statsX, statsRectY, statsRectWidth, statsRectHeight);

        //Wenn mehr als 10 Züge (0, 12-10 = 0, 2 -> index 2 bis 11) sonst 0 bis move.size()
        //also wird hier der Startindex ermittelt
        int start = Math.max(0, moves.size() - 10);
        for (int i = start; i < moves.size(); i++) {
            //Liefert boolean zurück ob gerade
            Predicate<Integer> isWhite = p -> p % 2 == 0;
            //Wenn gerade dann zug weiß sonst schwarz
            String color = isWhite.test(i) ? "Weiß" : "Schwarz";
            //aktueller move als text in variable speichern
            String moveText = moves.get(i).toString();

            //Y Position wird dynamisch ermittelt je nachdem wie vielter zug es ist
            int offset = i - start;
            int textY = statsY + 55 + offset * 20;

            //Wenn i gerade dann ist die Schriftfarbe weiß sonst schwarz
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(isWhite.test(i) ? Color.WHITE : Color.BLACK);

            //einzelteile zusammensetzen
            String label = color + ":";
            FontMetrics fm1 = g2.getFontMetrics();
            int labelWidth = fm1.stringWidth(label);

            //label und text zeichnen
            g2.drawString(label, statsX + 100, textY);
            g2.drawString(moveText, statsX + 100 + labelWidth + 10, textY);

            //Trennlinie erweiter sich dynamisch
            g2.setColor(Color.BLACK);
            g2.drawLine(statsX, statsY + 60 + offset * 20, statsX + 300, statsY + 60 + offset * 20);
        }
        g2.dispose();

    }

    public void setColor(boolean isWhite){
        this.color = isWhite;
    }

    //Methode um aktuelle züge in Uci Format darzustellen
    private String convertMoveListToUci(List<Move> moves) {
        StringBuilder sb = new StringBuilder();
        for (Move move : moves) {
            sb.append(move.toString()); //Check if ok       
        }
        return sb.toString();
    }

    //Methode um Uhren zu pausieren
    private void pauseClocks() {
        if (whiteRunning && whiteTimer != null) {
            whiteTimer.stop();
            whiteRunning = false;
        }
        if (blackRunning && blackTimer != null) {
            blackTimer.stop();
            blackRunning = false;
        }
        repaint();
    }


    private Square squareFromCoords(int rank, int file) {
        char fileChar = (char) ('A' + file);
        char rankChar = (char) ('1' + rank);
        String squareName = "" + fileChar + rankChar;
        return Square.valueOf(squareName);
    }

    //Methode um Uhren hinzuzufügen
    public void addClock(int timeType) {
        //Falls die Timer laufen sollen sie stoppen
        if(whiteTimer != null){
            whiteTimer.stop();
        }
        if(blackTimer != null){
            blackTimer.stop();
        }

        //vorgegeben Zeit in ms darstellen z.B. 3Min = 180.000ms
        long remaining = timeType * 60 * 1000;

        //Beide Uhren auf gleiche verbleibende Zeit setzen
        whiteRemaining = remaining;
        blackRemaining = remaining;

        //Alle 1000ms also jede sekunde wird refreshed
        whiteTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Jede Sekunde 1000ms von der verbleibenden Zeit abziehen
                whiteRemaining -= 1000;
                //Wenn Keine zeit mehr übrig, timer stoppen
                if (whiteRemaining <= 0) {
                    whiteRemaining = 0;
                    whiteRunning = false;
                    whiteTimer.stop();
                }
                //Ansicht aktualisieren
                repaint();
            }
        });
        //Alle 1000ms also jede sekunde wird refreshed
        blackTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Jede Sekunde 1000ms von der verbleibenden Zeit abziehen
                blackRemaining -= 1000;
                //Wenn keine Zeit mehr übrig, timer stoppen
                if (blackRemaining <= 0) {
                    blackRemaining = 0;
                    blackRunning = false;
                    blackTimer.stop();
                }
                //Ansicht aktualisieren
                repaint();
            }
        });
    }

    public void startClock() {
        if (moveHistory.size() > 1) {
            if (engine.getBoard().getSideToMove() == WHITE) {
                startBlackClock();
            } else {
                startWhiteClock();
            }
        } else {
            if (color) {
                startWhiteClock(); // <- problematisch
            } else {
                startBlackClock();
            }
        }
    }


    //Methode um weiße Uhr zu starten
    public void startWhiteClock(){
        //schwarze Uhr stoppen
        blackRunning = false;
        if(blackTimer != null){
            blackTimer.stop();
        }
        //weiße Uhr starten
        whiteRunning = true;
        if(whiteTimer != null){
            whiteTimer.start();
        }
    }

    private void startBlackClock(){
        //weiße Uhr stoppen
        whiteRunning = false;
        if(whiteTimer != null){
            whiteTimer.stop();
        }
        //schwarze Uhr starten
        blackRunning = true;
        if(blackTimer != null){
            blackTimer.start();
        }
    }

}
