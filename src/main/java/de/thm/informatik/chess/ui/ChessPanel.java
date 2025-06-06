package de.thm.informatik.chess.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TextArea;
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
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;

public class ChessPanel extends JPanel {

    private Timer countDownTimer;
    private long remaining;
    private boolean running;
    private ChessEngine engine = new ChessEngine();
    private Square selectedSquare = null;
    private final int squareSize = 95;
    private static final LinkedList<Move> moveHistory = new LinkedList<>();

    public ChessPanel() {
        setLayout(null);
        //Icons von FlatIcon
        ImageIcon fastForward = new ImageIcon(getClass().getResource("/Icons/fast-forward.png"));
        Image scaledForwardImage = fastForward.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledForward = new ImageIcon(scaledForwardImage);

        ImageIcon rewind = new ImageIcon(getClass().getResource("/Icons/rewind-button.png"));
        Image scaledRewindImage = rewind.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledRewind = new ImageIcon(scaledRewindImage);

        JButton forwardButton = new JButton(scaledForward);
        JButton rewindButton = new JButton(scaledRewind);

        forwardButton.setBounds(1420, 205, 30, 30);
        rewindButton.setBounds(1250, 205, 30, 30);

        add(forwardButton);
        add(rewindButton);

        //Buttons für forward und rewind TBC
        forwardButton.addActionListener(e -> System.out.println("fast forward"));
        rewindButton.addActionListener(e -> System.out.println("rewinded"));

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
                        moveHistory.add(move);
                        repaint();
                        System.out.println("Move executed: " + move);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Brett zeichnen
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

        //Figuren zeichnen
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

        //Timeranzeige
        if (running) {
            g.setColor(Color.RED);
            g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

            long sumSeconds = remaining / 1000;
            long sumMinutes = sumSeconds / 60;
            long seconds = sumSeconds % 60;

            String time = String.format("%02d:%02d", sumMinutes, seconds);
            g.drawString(time, 1300, 235);
        }

        //Stats Anzeige
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.setColor(Color.BLACK);

        List<Move> moves = getMoveHistory();

        int operationRectX = 1200;
        int operationRectY = 200;
        int operactionRectWidth = 300;
        int operationRectHeight = 40;

        int statsRectX = 1200; 
        int statsRectY = 240;  
        int statsRectWidth = 300;
        int statsRectHeight = Math.max(40, moves.size() * 20 + 20); // Dynamisch je nach Zuganzahl

        //Um Linien dicker zu machen
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3)); //3 Pixel dicke Linie
        g2.drawRect(operationRectX, operationRectY, operactionRectWidth, operationRectHeight);
        g2.drawRect(statsRectX, statsRectY, statsRectWidth, statsRectHeight);

        int yLine = 260;
        for (int i = 0; i < moves.size(); i++) {
            String color = (i % 2 == 0) ? "Weiß" : "Schwarz";
            String line = String.format("%s: %s", color, moves.get(i).toString());
            g2.drawString(line, 1300, 255 + i * 20);
            g2.drawLine(1200, yLine, 1500, yLine);
            yLine += 20;
        }
        g2.dispose(); //Ressourcen freigeben
    }

    private Square squareFromCoords(int rank, int file) {
        char fileChar = (char) ('A' + file);
        char rankChar = (char) ('1' + rank);
        String squareName = "" + fileChar + rankChar;
        return Square.valueOf(squareName);
    }

    public void addClock(int timeType) {
        if (countDownTimer != null && countDownTimer.isRunning()) {
            countDownTimer.stop();
        }

        switch (timeType) {
            case 3:
                remaining = 3 * 60 * 1000;
                break;
            case 5:
                remaining = 5 * 60 * 1000;
                break;
            case 10:
                remaining = 10 * 60 * 1000;
                break;
            default:
                System.out.println("Invalid");
                return;
        }
        running = true;

        countDownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remaining -= 1000;
                if (remaining <= 0) {
                    remaining = 0;
                    running = false;
                    countDownTimer.stop();
                }
                repaint();
            }
        });

        countDownTimer.start();
    }
}
