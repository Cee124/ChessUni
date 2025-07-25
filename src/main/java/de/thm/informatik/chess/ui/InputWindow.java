package de.thm.informatik.chess.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.bhlangonijr.chesslib.Board;

import de.thm.informatik.chess.domain.Facade;
import de.thm.informatik.chess.util.PieceIconLoader;

public class InputWindow extends JPanel {

    private final JButton modus3;
    private final JButton modus5;
    private final JButton modus10;

    private final JButton whiteKing;
    private final JButton blackKing;

    private final JLabel rewindText;
    private final JButton rewindBox;
    private boolean rewindSelected = false;
    
    private final JButton setupButton;
    private boolean setupSelected = false;

    private final JButton enter;
    private final JLabel toggleMoveOptionsText;
    private final JButton toggleMoveOptionsBox;
    private boolean moveOptionsSelected = false;
   
    private ChessPanel panel;
    private ClockHandler handler;
    private DrawBoard drawB;
    private Board customBoard;
    private Facade facade;

    public InputWindow() throws IOException {
       
        this.handler = new ClockHandler();
        this.panel = new ChessPanel(handler);
        this.drawB = panel.getDrawBoard();
        this.facade = panel.getFacade();

        setLayout(null);

        modus3 = new JButton("3 Min");
        modus5 = new JButton("5 Min");
        modus10 = new JButton("10 Min");
        whiteKing = new JButton(facade.getIcon("WHITEKING"));
        blackKing = new JButton(facade.getIcon("BLACKKING"));
        rewindText = new JLabel("Rewind? ");
        rewindBox = new JButton(facade.getIcon("EMPTY"));
        setupButton = new JButton("Setup Custom Position");
        toggleMoveOptionsText = new JLabel("Show Move Options? ");
        toggleMoveOptionsBox = new JButton(facade.getIcon("EMPTY"));

        enter = new JButton("Enter");

        add(modus3);
        add(modus5);
        add(modus10);
        add(whiteKing);
        add(blackKing);
        add(rewindText);
        add(rewindBox);
        add(toggleMoveOptionsText);
        add(toggleMoveOptionsBox);
        add(enter);
        add(setupButton);

        modus3.addActionListener(e -> {
            handler.addClock(3);
        });
        modus5.addActionListener(e -> {
            handler.addClock(5);
        });
        modus10.addActionListener(e -> {
            handler.addClock(10);
        });
        whiteKing.addActionListener(e -> panel.setColor(true));
        blackKing.addActionListener(e -> panel.setColor(false));

        rewindBox.addActionListener(e -> {
            rewindSelected = !rewindSelected;

            if (rewindSelected) {
                rewindBox.setIcon(facade.getIcon("TICKED"));
                panel.setRewind(true);

            } else {
                rewindBox.setIcon(facade.getIcon("EMPTY"));

                panel.setRewind(false);
            }
        });
        
        setupButton.addActionListener(e -> {
            setupSelected = true;
            
            JFrame framePanel = new JFrame("Custom Position");
            
            SetupPositionPanel setupPanel = new SetupPositionPanel(framePanel, customBoard -> {
                this.customBoard = customBoard;
                panel.setCustomBoard(customBoard);
                
            });

            
            framePanel.add(setupPanel);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            framePanel.setSize(screenSize);
            framePanel.setLocation(0, 0);

            framePanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            framePanel.setVisible(true);
            
            
        });

        toggleMoveOptionsBox.addActionListener(e -> {
            moveOptionsSelected = !moveOptionsSelected;
            if (moveOptionsSelected) {
                toggleMoveOptionsBox.setIcon(facade.getIcon("TICKED"));
                drawB.setShowMoveOptions(true);
            } else {
                toggleMoveOptionsBox.setIcon(facade.getIcon("EMPTY"));
                drawB.setShowMoveOptions(false);
            }

        });


        enter.addActionListener(e -> {
        	
        	if (setupSelected && customBoard != null) {
                panel.setCustomBoard(customBoard);
                
            }
        	
            // Schließen des Input-Fensters
            JFrame topFrame = (JFrame) getTopLevelAncestor();
            topFrame.dispose();

            JFrame framePanel = new JFrame("Chess");
            framePanel.add(panel);

            //Bildschirmgröße holen und setzen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            framePanel.setSize(screenSize);
            framePanel.setLocation(0, 0);

            framePanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            framePanel.setVisible(true);
        });
    }

    @Override
    public void doLayout() {
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
        
        setupButton.setBounds(centerX - 50, 5, buttonWidth + 100, buttonHeight);

        enter.setBounds(0, panelHeight - 40, panelWidth, 40);
        rewindText.setBounds(centerX + 15, 115, 100, 20);
        rewindBox.setBounds(centerX + 25, 150, 30, 30);

        toggleMoveOptionsText.setBounds(centerX - 10, 190, 160, 20);
        toggleMoveOptionsBox.setBounds(centerX + 25, 225, 30, 30);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Color color = new Color(100, 150, 100);
        g2.setColor(color);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
