package de.thm.informatik.chess.ui;

import javax.swing.JFrame;

public class MainWindow {
    public static void main(String[] args) {

        //Initialisieren und Deklarieren eines neuen Board Fensters
        Board board = new Board();

        JFrame jf = new JFrame("Chess Board (8 x 8)");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jf.add(board);
        board.addClock(5);
        jf.setVisible(true);

    }
}
