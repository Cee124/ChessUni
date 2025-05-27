package de.thm.informatik.chess.ui;

import javax.swing.JFrame;

public class TestMain {
    public static void main(String[] args) {
        Board board = new Board();

        JFrame jf = new JFrame("Chess Board (8 x 8)");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jf.add(board);
        board.addClock(5);
        jf.setVisible(true);

    }
}
