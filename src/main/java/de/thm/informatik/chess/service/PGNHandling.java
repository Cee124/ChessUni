package de.thm.informatik.chess.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import de.thm.informatik.chess.domain.ChessEngine;
import de.thm.informatik.chess.ui.ChessPanel;
import de.thm.informatik.chess.util.UciToSAN;


public class PGNHandling {

    private final String filePath;

    private static final Logger logger = LogManager.getLogger(ChessPanel.class);

    public PGNHandling(String filePath) {
        this.filePath = filePath;
    }

    public void saveGame(List<Move> moves) {
        Board board = new Board();
        StringBuilder uciMoves = new StringBuilder();

        // UCI-Zugfolge aus den Move-Objekten zusammenbauen
        for (Move move : moves) {
            uciMoves.append(move.getFrom().toString().toLowerCase());
            uciMoves.append(move.getTo().toString().toLowerCase());
        }

        // Zugnotation in SAN konvertieren
        String annotatedMovesRaw = UciToSAN.convertUciToAnnotatedMoves(uciMoves.toString());

        // SAN-Notation hübsch formatieren mit Zugnummern
        StringBuilder formattedPgn = new StringBuilder();
        String[] tokens = annotatedMovesRaw.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.matches("\\d+")) {
                if (formattedPgn.length() > 0) {
                    formattedPgn.append(" ");
                }
                formattedPgn.append(token).append(".");
            } else {
                formattedPgn.append(token);
                if (i + 1 < tokens.length && !tokens[i + 1].matches("\\d+")) {
                    formattedPgn.append(" ");
                }
            }
        }

        // Ergebnis bestimmen
        String result = "*";
        if (!moves.isEmpty()) {
            Board finalBoard = new Board();
            for (Move move : moves) {
                finalBoard.doMove(move);
            }

            if (finalBoard.isMated()) {
                result = finalBoard.getSideToMove() == com.github.bhlangonijr.chesslib.Side.WHITE ? "0-1" : "1-0";
            } else if (finalBoard.isDraw()) {
                result = "1/2-1/2";
            }
        }

        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write("[Event \"User Game\"]\n");
            writer.write("[Site \"Local\"]\n");
            writer.write("[Date \"" + java.time.LocalDate.now() + "\"]\n");
            writer.write("[Round \"1\"]\n");
            writer.write("[White \"Player1\"]\n");
            writer.write("[Black \"Player2\"]\n");
            writer.write("[Result \"" + result + "\"]\n\n");
            writer.write(formattedPgn.toString().trim() + " " + result + "\n\n");
        } catch (IOException e) {
            logger.error("Couldn't write to file");
        }

        logger.info("Gespeichert unter: {} ", file.getAbsolutePath());
    }



    public static void loadGame(String filePath, ChessEngine engine) {
        logger.info("Starte Ladevorgang der PGN-Datei: {}", filePath);

        try {
            // Lade die PGN-Datei
            PgnHolder pgnHolder = new PgnHolder(filePath);
            pgnHolder.loadPgn();
            logger.info("PGN-Datei erfolgreich geladen.");

            // Prüfe, ob mindestens eine Partie vorhanden ist
            if (pgnHolder.getGames().isEmpty()) {
                logger.info("Keine Partien gefunden.");
                return;
            }else {
            	logger.info("Eine Partie gefunden");
            }

            // Nimm die erste gefundene Partie
            var game = pgnHolder.getGames().get(0);
            var halfMoves = game.getHalfMoves();

            logger.info("Anzahl Züge in der geladenen Partie: {}", halfMoves.size());

            // Neues Brett starten
            Board board = new Board();

            // Leere alte Zughistorie
            ChessPanel.getMoveHistory().clear();

            // Iteriere über alle Züge
            for (int i = 0; i < halfMoves.size(); i++) {
                var pgnMove = halfMoves.get(i);

                Move move = new Move(pgnMove.getFrom(), pgnMove.getTo(), pgnMove.getPromotion());

                if (!board.isMoveLegal(move, false)) {
                    logger.error("Illegaler Zug beim Laden: {} (Zug {} )", move, (i + 1));
                    break;
                }

                board.doMove(move);
                ChessPanel.getMoveHistory().add(move); 
            }

            // Setze das fertige Board in die Engine
            engine.setBoard(board);
            
            

        } catch (Exception e) {
            logger.error("Fehler beim Laden der PGN-Datei: {}", e.getMessage());
        }
    }
}