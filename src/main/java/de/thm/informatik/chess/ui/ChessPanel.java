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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.github.bhlangonijr.chesslib.Piece;
import static com.github.bhlangonijr.chesslib.Side.WHITE;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.domain.Opening;
import de.thm.informatik.chess.domain.OpeningDetection;

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
    private Opening detectedOpening = null;

    private List<Opening> openings;

    public ChessPanel() throws IOException {
        detector = new OpeningDetection();

        Map<String, String> openingMap = detector.loadOpenings("/Openings/eco_openings.html");
        if(openingMap.isEmpty()){
            System.out.println("LEEEEEEEEEEEEEEEEEER");
        }else{
            openingMap.forEach((uci, name) -> System.out.println(uci + " : " + name));
        }

        setLayout(null);

        forwardButton = new JButton(IconLoader.FORWARD_ICON);
        rewindButton = new JButton(IconLoader.REWIND_ICON);
        startButton = new JButton(IconLoader.START_ICON);
        pauseButton = new JButton(IconLoader.PAUSE_ICON);

        whiteKing = new JButton(IconLoader.WHITEKING_ICON);
        blackKing = new JButton(IconLoader.BLACKKING_ICON);
        
        add(forwardButton);
        add(rewindButton);
        add(startButton);
        add(pauseButton);

        add(whiteKing);
        add(blackKing);

        //Buttons für forward und rewind TBC
        forwardButton.addActionListener(e -> fastForwardMove());
        rewindButton.addActionListener(e -> rewindMove());
        startButton.addActionListener(e -> startWhiteClock());
        pauseButton.addActionListener(e -> pauseClocks());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int file = e.getX() / squareSize;
                int rank = 7 - (e.getY() / squareSize);
                Square clickedSquare = squareFromCoords(rank, file);

                if (selectedSquare == null) {
                    if (engine.getPiece(clickedSquare) != Piece.NONE) {
                        selectedSquare = clickedSquare;
                        repaint();
                    }
                } else {
                    Move move = new Move(selectedSquare, clickedSquare);
                    List<Move> legalMoves = engine.getLegalMoves();

                    if (legalMoves.contains(move)) {
                        engine.makeMove(move);
                        moveHistory.subList(currentMoveIndex, moveHistory.size()).clear();
                        moveHistory.add(move);
                        currentMoveIndex = moveHistory.size();
                        
                        //Debug Ausgabe
                        System.out.println("=== Aktuelle MoveHistory ===");
                        for (Move m : moveHistory) {
                            System.out.println(m.toString());
                        }

                        repaint();

                        System.out.println("Move executed: " + move);

                        if(engine.getBoard().getSideToMove() == WHITE){
                            startWhiteClock();
                        }else{
                            startBlackClock();
                        }
                    } else {
                        System.out.println("Illegal move: " + move);
                    }
                    selectedSquare = null;
                    repaint();
                }
            }
        });
    }

    public static LinkedList<Move> getMoveHistory() {
        return moveHistory;
    }

    private void rewindMove(){
        if(currentMoveIndex > 0){
            currentMoveIndex--;
            engine.reset();
            for(int i = 0; i < currentMoveIndex; i++){
                engine.makeMove(moveHistory.get(i));
            }
            repaint();  
        }
    }

    private void fastForwardMove(){
        List<Move> moveHistoryForward = getMoveHistory();

        if(currentMoveIndex < moveHistoryForward.size()){
            currentMoveIndex++;
            Move currentMove = moveHistoryForward.get(currentMoveIndex);
            engine.makeMove(currentMove);
            repaint();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Rechte Bildschirmhälfte dunkelgrün färben
        Graphics2D g2 = (Graphics2D) g.create();
        Color mattDunkelgruen = new Color(30, 60, 30);
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g2.setColor(mattDunkelgruen);
        g2.fillRect(panelWidth / 2, 0, panelWidth / 2, panelHeight);

        int boardRightEdge = 8 * squareSize;
        int boardBottomEdge = 8 * squareSize;
    
        
        int clockX = boardRightEdge + 50;
        int whiteClockY = boardBottomEdge - 50;
        int blackClockY = 50;                   
    
        int statsX = getWidth() - 300 - 100;
        int statsY = 200;

        //Opening Detection Window
        int openingRectX = statsX;
        int openingRectY = statsY - 100;
        int openingRectWidth = 300;
        int openingRectHeight = 40;

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(openingRectX, openingRectY, openingRectWidth, openingRectHeight);

        String openingText = (detectedOpening != null) ? detectedOpening.toString() : "Keine Eröffnung erkannt";
        
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(openingText);

        g2.drawString(openingText, openingRectX + (openingRectWidth - textWidth) / 2, openingRectY + 25);

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

        whiteKing.setBounds(centerInStats +60 + 40, buttonY, 30, 30);
        blackKing.setBounds(centerInStats - 30 - 20, buttonY, 30, 30);

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
                Square sq = squareFromCoords(rank, file);
                Piece piece = engine.getBoard().getPiece(sq);
                if (piece != Piece.NONE) {
                    Image img = PieceImageLoader.getImage(piece);
                    if (img != null) {
                        int x = file * squareSize;
                        int y = (7 - rank) * squareSize;
                        g.drawImage(img, x, y, squareSize, squareSize, this);
                    }
                }
            }
        }

        //Draw clocks
        g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

        //White clock
        g.setColor(whiteRunning ? Color.RED : Color.BLACK);
        long whiteSumSeconds = whiteRemaining / 1000;
        long whiteSumMinutes = whiteSumSeconds / 60;
        long whiteSeconds = whiteSumSeconds % 60;
        String whiteTime = String.format("%02d:%02d", whiteSumMinutes, whiteSeconds);
        g.drawString(whiteTime, clockX, whiteClockY);

        //Black clock
        g.setColor(blackRunning ? Color.RED : Color.BLACK);
        long blackSumSeconds = blackRemaining / 1000;
        long blackSumMinutes = blackSumSeconds / 60;
        long blackSeconds = blackSumSeconds % 60;
        String blackTime = String.format("%02d:%02d", blackSumMinutes, blackSeconds);
        g.drawString(blackTime, clockX, blackClockY);
    
        //Draw stats display
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.setColor(Color.BLACK);

        List<Move> moves = getMoveHistory();

        int operationRectX = statsX;
        int operationRectY = statsY;
        int operactionRectWidth = 300;
        int operationRectHeight = 40;

        int statsRectX = statsX; 
        int statsRectY = statsY + 40;  
        int statsRectWidth = 300;
        int statsRectHeight = Math.max(40, moves.size() * 20 + 20);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(operationRectX, operationRectY, operactionRectWidth, operationRectHeight);
        g2.drawRect(statsRectX, statsRectY, statsRectWidth, statsRectHeight);

        int yLine = statsY + 60;
        for (int i = 0; i < moves.size(); i++) {
            Predicate<Integer> isWhite = p -> p % 2 == 0;
            String color = isWhite.test(i) ? "Weiß" : "Schwarz";
            String moveText = moves.get(i).toString();

            int textY = statsY + 55 + i * 20;

            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(isWhite.test(i) ? Color.WHITE : Color.BLACK);

            String label = color + ":";
            FontMetrics fm1 = g2.getFontMetrics();
            int labelWidth = fm1.stringWidth(label);

            g2.drawString(label, statsX + 100, textY);
            g2.drawString(moveText, statsX + 100 + labelWidth + 10, textY);

            //Trennlinie
            g2.setColor(Color.BLACK);
            g2.drawLine(statsX, yLine, statsX + 300, yLine);
            yLine += 20;
        }
        g2.dispose();

    }

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

    public void addClock(int timeType) {
        if(whiteTimer != null){
            whiteTimer.stop();
        }
        if(blackTimer != null){
            blackTimer.stop();
        }

        long remaining = timeType * 60 * 1000;

        whiteRemaining = remaining;
        blackRemaining = remaining;

        whiteTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whiteRemaining -= 1000;
                if (whiteRemaining <= 0) {
                    whiteRemaining = 0;
                    whiteRunning = false;
                    whiteTimer.stop();
                }
                repaint();
            }
        });

        blackTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blackRemaining -= 1000;
                if (blackRemaining <= 0) {
                    blackRemaining = 0;
                    blackRunning = false;
                    blackTimer.stop();
                }
                repaint();
            }
        });
    }

    public void startWhiteClock(){
        blackRunning = false;

        if(blackTimer != null){
            blackTimer.stop();
        }

        whiteRunning = true;
        if(whiteTimer != null){
            whiteTimer.start();
        }
    }

    private void startBlackClock(){
        whiteRunning = false;

        if(whiteTimer != null){
            whiteTimer.stop();
        }

        blackRunning = true;
        if(blackTimer != null){
            blackTimer.start();
        }
    }

}
