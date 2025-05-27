package de.thm.informatik.chess.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel{

    private Timer countDownTimer;
    private long remaining;
    private boolean running;

    public void addClock(int timeType){
        if(countDownTimer != null && countDownTimer.isRunning()){
            countDownTimer.stop();
        }

        //Je nach übergebenen Wert Timer für 3, 5 oder 10 Minuten
        switch(timeType){
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

        //Timer um runterzuzählen, es werden alle 1000 Millisekunden 1 sekunde von der 'remaining' Zeit abgezogen
        countDownTimer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                remaining -= 1000;
                if(remaining <= 0){
                    remaining = 0;
                    running = false;
                    countDownTimer.stop();
                }
                repaint();
            }
        });

        countDownTimer.start();
    }
    
    @Override
    //Graphics g ist Zeichenelement mit dem man auf das Panel 'zeichnen' kann
    protected void paintComponent(Graphics g){
        //entfernt altes, wischt quasi die tafel sauber und alles was grafisch dargestellt werden soll muss hier drin sein
        super.paintComponent(g);
        //nach gefühl, nicht final
        int squareSize = 95;

        //2D Erstellung für Schachbrett, abwechselnd weißer und grauer Block
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                //wenn gerade dann grau, sonst weiß, für abwechslung
                if((i + j) % 2 == 0){
                    g.setColor(Color.lightGray);
                }else{
                    g.setColor(Color.white);
                }
                //erstellung des jeweiligen feldes
                g.fillRect(j * squareSize, i * squareSize, squareSize, squareSize);
            }
        }

        if(running){
            //Timer Farbe
            g.setColor(Color.RED);
            //Timer stats aka css like
            g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 40));

            //gesamtAnzahl der verbleibenden Sekunden
            long sumSeconds = remaining / 1000;
            //anzahl der Minuten
            long sumMinutes = sumSeconds / 60;
            //anzahl der Sekunden
            long seconds = sumSeconds % 60;

            //Erstellung der Anzeige
            String time = String.format("%02d:%02d", sumMinutes, seconds);
            //Angabe wo was abgebildet werden soll
            g.drawString(time, 1075, 150);
        }
    }
}
