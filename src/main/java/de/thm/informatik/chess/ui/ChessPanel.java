package de.thm.informatik.chess.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.util.PieceImageLoader;

public class ChessPanel extends JPanel {

    private Timer countDownTimer;
    private long remaining;
    private boolean running;
    private ChessEngine engine = new ChessEngine();
    private Square selectedSquare = null;
    private final int squareSize = 95;
    public ChessPanel() {
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

    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Brett zeichnen
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

        // Figuren zeichnen
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

        // Timeranzeige
        if (running) {
            g.setColor(Color.RED);
            g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

            long sumSeconds = remaining / 1000;
            long sumMinutes = sumSeconds / 60;
            long seconds = sumSeconds % 60;

            String time = String.format("%02d:%02d", sumMinutes, seconds);
            g.drawString(time, 1075, 150);
        }
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
