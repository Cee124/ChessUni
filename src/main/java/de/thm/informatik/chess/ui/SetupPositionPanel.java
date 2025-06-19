package de.thm.informatik.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;

public class SetupPositionPanel extends JPanel {

	private final int squareSize = 95;
	
	private boolean color = true;

    private final Board board;
    private Piece selectedPiece = Piece.NONE;

    //private final Map<Piece, Icon> pieceIcons;
    private final JPanel selectionPanel;

    private final JButton doneButton;
    private JFrame parentFrame;
    
    //Konstruktor
    public SetupPositionPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        this.board = new Board();
        board.clear(); // Leeres Board

        setLayout(new BorderLayout());
        /*
        // Icons vorbereiten
        pieceIcons = new HashMap<>();
        pieceIcons.put(Piece.WHITE_PAWN, IconLoader.WHITEPAWN_ICON);
        pieceIcons.put(Piece.WHITE_KNIGHT, IconLoader.WHITEKNIGHT_ICON);
        pieceIcons.put(Piece.WHITE_BISHOP, IconLoader.WHITEBISHOP_ICON);
        pieceIcons.put(Piece.WHITE_ROOK, IconLoader.WHITEROOK_ICON);
        pieceIcons.put(Piece.WHITE_QUEEN, IconLoader.WHITEQUEEN_ICON);
        pieceIcons.put(Piece.WHITE_KING, IconLoader.WHITEKING_ICON);

        pieceIcons.put(Piece.BLACK_PAWN, IconLoader.BLACKPAWN_ICON);
        pieceIcons.put(Piece.BLACK_KNIGHT, IconLoader.BLACKKNIGHT_ICON);
        pieceIcons.put(Piece.BLACK_BISHOP, IconLoader.BLACKBISHOP_ICON);
        pieceIcons.put(Piece.BLACK_ROOK, IconLoader.BLACKROOK_ICON);
        pieceIcons.put(Piece.BLACK_QUEEN, IconLoader.BLACKQUEEN_ICON);
        pieceIcons.put(Piece.BLACK_KING, IconLoader.BLACKKING_ICON);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int file = e.getX() / TILE_SIZE;
                int rank = 7 - (e.getY() / TILE_SIZE);
                if (file >= 0 && file < 8 && rank >= 0 && rank < 8) {
                    Square square = Square.at(rank, file);
                    if (selectedPiece == Piece.NONE) {
                        board.setPiece(square, Piece.NONE);
                    } else {
                        board.setPiece(square, selectedPiece);
                    }
                    boardPanel.repaint();
                }
            }
        });
*/
        // Rechts: Auswahlpanel + Done Button
        selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(8, 2, 5, 5));
        //addPieceButtons();

        // Done-Button
        doneButton = new JButton("Done");
        doneButton.addActionListener(e -> {
            // Fenster schließen
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            // Optional: CustomBoard irgendwo speichern/zurückgeben
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(selectionPanel, BorderLayout.CENTER);
        rightPanel.add(doneButton, BorderLayout.SOUTH);

        //add(boardPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    public void doLayout() {
    	
    }
    
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
    			
    			//Draw chess board
    	        for (int rank = 0; rank < 8; rank++) {
    	            for (int file = 0; file < 8; file++) {
    	                int drawRank = color ? 7 - rank : rank;
    	                int drawFile = color ? file : 7 - file;

    	                if ((rank + file) % 2 == 0) {
    	                    g.setColor(Color.lightGray);
    	                } else {
    	                    g.setColor(Color.white);
    	                }
    	                g.fillRect(drawFile * squareSize, drawRank * squareSize, squareSize, squareSize);
    	            }
    	        }
    }

    /*
    
    private void addPieceButtons() {
        for (Piece piece : pieceIcons.keySet()) {
            JButton pieceButton = new JButton(pieceIcons.get(piece));
            pieceButton.setToolTipText(piece.toString());
            pieceButton.addActionListener(e -> selectedPiece = piece);
            selectionPanel.add(pieceButton);
        }

        // Leeren Platzierer hinzufügen
        JButton emptyButton = new JButton("X");
        emptyButton.setToolTipText("Feld leeren");
        emptyButton.addActionListener(e -> selectedPiece = Piece.NONE);
        selectionPanel.add(emptyButton);
    }
    
    */

    public Board getCustomBoard() {
        return board;
    }
}


