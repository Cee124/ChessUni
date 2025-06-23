package de.thm.informatik.chess.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.github.bhlangonijr.chesslib.Side;


import de.thm.informatik.chess.domain.Facade;

import static com.github.bhlangonijr.chesslib.Side.WHITE;

public class ClockHandler {

    private ChessPanel panel;
    private Facade facade; 
    

    private Timer whiteTimer;
    private Timer blackTimer;

    private boolean whiteRunning = false;
    private boolean blackRunning = false;
    private long whiteRemaining;
    private long blackRemaining;

    private boolean color = true;

    public ClockHandler(){
    }

    public void setPanel(ChessPanel panel){
        this.panel = panel;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public void setColor(boolean isWhite){
        this.color = isWhite;
    }
    
    public void setWhiteRemaining(long time) {
        this.whiteRemaining = time;
    }

    public void setBlackRemaining(long time) {
        this.blackRemaining = time;
    }

    //Methode um Uhren zu pausieren
    public void pauseClocks() {
        if (whiteRunning && whiteTimer != null) {
            whiteTimer.stop();
            whiteRunning = false;
        }
        if (blackRunning && blackTimer != null) {
            blackTimer.stop();
            blackRunning = false;
        }
        if(panel != null){
            panel.repaint();
        }
    }

    //Methode um Uhren hinzuzufügen
    public void addClock(int timeType) {
        //Falls die Timer laufen sollen sie stoppen
        if(whiteTimer != null){
            whiteTimer.stop();
        }
        if(blackTimer != null){
            blackTimer.stop();
        }

        //vorgegeben Zeit in ms darstellen z.B. 3Min = 180.000ms
        long remaining = timeType * 60 * 1000;

        //Beide Uhren auf gleiche verbleibende Zeit setzen
        whiteRemaining = remaining;
        blackRemaining = remaining;

        //Alle 1000ms also jede sekunde wird refreshed
        whiteTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Jede Sekunde 1000ms von der verbleibenden Zeit abziehen
                whiteRemaining -= 1000;
                //Wenn Keine zeit mehr übrig, timer stoppen
                if (whiteRemaining <= 0) {
                    whiteRemaining = 0;
                    whiteRunning = false;
                    whiteTimer.stop();
                }
                //Ansicht aktualisieren
                panel.repaint();
            }
        });
        //Alle 1000ms also jede sekunde wird refreshed
        blackTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Jede Sekunde 1000ms von der verbleibenden Zeit abziehen
                blackRemaining -= 1000;
                //Wenn keine Zeit mehr übrig, timer stoppen
                if (blackRemaining <= 0) {
                    blackRemaining = 0;
                    blackRunning = false;
                    blackTimer.stop();
                }
                //Ansicht aktualisieren
                panel.repaint();
            }
        });
    }

    public void startClocks() {
        //Stoppe beide Uhren
        pauseClocks();
        
        if (facade.getMoveHistory().isEmpty()) {
            if(color){
                startWhiteClock();
            }else{
                startBlackClock();
            }
        }else {
            //Wenn bereits Züge gemacht wurden, starte die Uhr für die aktuelle Seite
            Side sideToMove = facade.getBoard().getSideToMove();
            if (sideToMove == WHITE) {
                if(color){
                    startWhiteClock();
                }else{
                    startBlackClock();
                }
            }else {
                if(color){
                    startBlackClock();
                }else{
                    startWhiteClock();
                }
            }
        }
    }

    //Methode um weiße Uhr zu starten
    public void startWhiteClock(){
        //schwarze Uhr stoppen
        blackRunning = false;
        if(blackTimer != null){
            blackTimer.stop();
        }
        //weiße Uhr starten
        whiteRunning = true;
        if(whiteTimer != null){
            whiteTimer.start();
        }
    }

    public void startBlackClock(){
         //weiße Uhr stoppen
        whiteRunning = false;
        if(whiteTimer != null){
            whiteTimer.stop();
        }
        //schwarze Uhr starten
        blackRunning = true;
        if(blackTimer != null){
            blackTimer.start();
        }
    }

    public void updateClocks(){
        pauseClocks();
        Side currentSide = facade.getBoard().getSideToMove();
        if (panel.color) {
            if (currentSide == Side.WHITE) {
                startWhiteClock();
            } else {
                startBlackClock();
            }
        } else {
            if (currentSide == Side.WHITE) {
                startBlackClock();
            } else {
                startWhiteClock();
            }
        }
            
    }

    public long getWhiteRemaining() {
        return whiteRemaining;
    }

    public long getBlackRemaining() {
        return blackRemaining;
    }

    public boolean isWhiteRunning() {
        return whiteRunning;
    }

    public boolean isBlackRunning() {
        return blackRunning;
    }

}
