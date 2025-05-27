package de.thm.informatik.chess.domain;

public abstract class Figur {
    private final boolean white;
    private int x;
    private int y;

    public Figur(boolean white, int x, int y){
        this.white = white;
        this.x = x;
        this.y = y;
    }

    public abstract boolean validMove(int xNew, int yNew, Figur[][] board);

    public boolean isWhite(){
        return white;
    }

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    }

}
