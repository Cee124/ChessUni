package de.thm.informatik.chess.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.github.bhlangonijr.chesslib.Piece;
import static com.github.bhlangonijr.chesslib.Side.WHITE;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;

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

    private int currentMoveIndex;

    public ChessPanel() {
        setLayout(null);
        //Icons von FlatIcon
        ImageIcon fastForward = new ImageIcon(getClass().getResource("/Icons/fast-forward.png"));
        Image scaledForwardImage = fastForward.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledForward = new ImageIcon(scaledForwardImage);

        ImageIcon rewind = new ImageIcon(getClass().getResource("/Icons/rewind-button.png"));
        Image scaledRewindImage = rewind.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledRewind = new ImageIcon(scaledRewindImage);

        ImageIcon start = new ImageIcon(getClass().getResource("/Icons/play.png"));
        Image scaledStartImage = start.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledStartButton = new ImageIcon(scaledStartImage);

        ImageIcon pause = new ImageIcon(getClass().getResource("/Icons/pause.png"));
        Image scaledPauseImage = pause.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledPause = new ImageIcon(scaledPauseImage);

        forwardButton = new JButton(scaledForward);
        rewindButton = new JButton(scaledRewind);
        startButton = new JButton(scaledStartButton);
        pauseButton = new JButton(scaledPause);
        
        add(forwardButton);
        add(rewindButton);
        add(startButton);
        add(pauseButton);

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

        int boardRightEdge = 8 * squareSize;
        int boardBottomEdge = 8 * squareSize;
    
        
        int clockX = boardRightEdge + 50;
        int whiteClockY = boardBottomEdge - 50;
        int blackClockY = 50;                   
    
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

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(operationRectX, operationRectY, operactionRectWidth, operationRectHeight);
        g2.drawRect(statsRectX, statsRectY, statsRectWidth, statsRectHeight);

        int yLine = statsY + 60;
        for (int i = 0; i < moves.size(); i++) {
            String color = (i % 2 == 0) ? "Weiß" : "Schwarz";
            String line = String.format("%s: %s", color, moves.get(i).toString());
            g2.drawString(line, statsX + 100, statsY + 55 + i * 20);
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
