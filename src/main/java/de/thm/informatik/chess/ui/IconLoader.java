package de.thm.informatik.chess.ui;

import java.awt.Image;

import javax.swing.ImageIcon;

public class IconLoader {

    public static ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(IconLoader.class.getResource(path));
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static final ImageIcon FORWARD_ICON = loadIcon("/Icons/fast-forward.png", 30, 30);
    public static final ImageIcon REWIND_ICON = loadIcon("/Icons/rewind-button.png", 30, 30);
    public static final ImageIcon START_ICON = loadIcon("/Icons/play.png", 30, 30);
    public static final ImageIcon PAUSE_ICON = loadIcon("/Icons/pause.png", 30, 30);

    public static final ImageIcon WHITEKING_ICON = loadIcon("/Images/wk.png", 30, 30);
    public static final ImageIcon BLACKKING_ICON = loadIcon("/Images/bk.png", 30, 30);

}
