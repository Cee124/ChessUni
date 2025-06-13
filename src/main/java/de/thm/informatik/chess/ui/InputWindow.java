package de.thm.informatik.chess.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InputWindow extends JPanel {

    private final JButton modus3;
    private final JButton modus5;
    private final JButton modus10;

    private final JButton whiteKing;
    private final JButton blackKing;

    private final JLabel rewindText;
    private final JButton rewindBox;
    private boolean rewindSelected = false;

    private final JButton enter;

    public InputWindow(){
        setLayout(null);

        modus3 = new JButton("3 Min");
        modus5 = new JButton("5 Min");
        modus10 = new JButton("10 Min");

        whiteKing = new JButton(IconLoader.WHITEKING_ICONX);
        blackKing = new JButton(IconLoader.BLACKKING_ICONX);

        rewindText = new JLabel("Rewind? ");
        rewindBox = new JButton(IconLoader.EMPTY_ICON);

        enter = new JButton("Enter");

        add(modus3);
        add(modus5);
        add(modus10);
        add(whiteKing);
        add(blackKing);
        add(rewindText);
        add(rewindBox);
        add(enter);

        modus3.addActionListener(e -> System.out.println("3 Minuten"));
        modus5.addActionListener(e -> System.out.println("5 Minuten"));
        modus10.addActionListener(e -> System.out.println("10 Minuten"));

        whiteKing.addActionListener(e -> System.out.println("White"));
        blackKing.addActionListener(e -> System.out.println("Black"));

        enter.addActionListener(e -> System.out.println("Enter"));

        rewindBox.addActionListener(e -> {
            rewindSelected = !rewindSelected;
            if(rewindSelected){
                rewindBox.setIcon(IconLoader.TICKED_ICON);
            }else{
                rewindBox.setIcon(IconLoader.EMPTY_ICON);
            }
        });
    }

    @Override
    public void doLayout(){
        super.doLayout();

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int space = 40;
        int buttonWidth = 80;
        int buttonHeight = 30;
        int centerX = (panelWidth - buttonWidth) / 2;

        modus3.setBounds(centerX - buttonWidth - space, 40, buttonWidth, buttonHeight);
        modus5.setBounds(centerX, 40, buttonWidth, buttonHeight);
        modus10.setBounds(centerX + buttonWidth + space, 40, buttonWidth, buttonHeight);

        int modus3X = centerX - buttonWidth - space;
        int modus10X = centerX + buttonWidth + space;

        whiteKing.setBounds(modus10X + (buttonWidth - 70), 120, 60, 60);
        blackKing.setBounds(modus3X + (buttonWidth - 70), 120, 60, 60);

        enter.setBounds(0, panelHeight - 40, panelWidth, 40);
        rewindText.setBounds(centerX + 15, 115, 100, 20);
        rewindBox.setBounds(centerX + 25, 150, 30, 30);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Color color = new Color(100, 150, 100);
        g2.setColor(color);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
