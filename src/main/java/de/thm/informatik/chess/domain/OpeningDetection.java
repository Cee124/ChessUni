package de.thm.informatik.chess.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

public class OpeningDetection {

    public List<Opening> loadOpenings(InputStream inputStream) throws IOException {
        List<Opening> openings = new ArrayList<>();

        Document doc = Jsoup.parse(inputStream, "UTF-8", "");
        Elements boldElements = doc.select("b");

        for (Element bold : boldElements) {
            String headerText = bold.text().trim();

            // Nur Einträge mit ECO-Code (z. B. B66)
            if (!headerText.matches("^[A-E]\\d{2} .*")) continue;

            String[] parts = headerText.split(" ", 2);
            if (parts.length < 2) continue;

            String eco = parts[0].trim();
            String name = parts[1].trim();

            // Zugfolge aus nächstem Textknoten suchen
            Node next = bold.nextSibling();
            while (next != null && (next.nodeName().equals("br") || next.toString().trim().isEmpty())) {
                next = next.nextSibling();
            }

            if (next != null && next.nodeName().equals("#text")) {
                String moveText = next.toString().trim();
                if (!moveText.isEmpty()) {
                    List<String> uciMoves = convertToUciMoves(moveText);
                    if(!uciMoves.isEmpty()){
                        openings.add(new Opening(eco, name, uciMoves));
                    }
                }
            }
        }
        return openings;
    }

    public Opening detectOpening(List<Move> playedMoves, List<Opening> openings){
        Opening bestMatch = null;
        int maxMatchedMoves = -1;

        for(Opening opening : openings){
            if(opening.matches(playedMoves)){
                if(opening.getMoves().size() > maxMatchedMoves){
                    bestMatch = opening;
                    maxMatchedMoves = opening.getMoves().size();
                }
            }
        }
        return bestMatch;
    }

    private List<String> convertToUciMoves(String moveText) {
        List<String> uciMoves = new ArrayList<>();

        try {
            // PGN in temporäre Datei schreiben, da PgnHolder nur mit Datei arbeitet
            File tempPgn = File.createTempFile("opening", ".pgn");
            tempPgn.deleteOnExit();

            String pgn = "[Event \"Opening\"]\n\n" + moveText + " *";
            java.nio.file.Files.write(tempPgn.toPath(), pgn.getBytes());

            // Mit chesslib laden
            PgnHolder holder = new PgnHolder(tempPgn.getAbsolutePath());
            holder.loadPgn();

            if (holder.getGames().isEmpty()) {
                System.out.println("Konnte keine Partien parsen.");
                return uciMoves;
            }

            Game game = holder.getGames().get(0);
            Board board = new Board();

            for (Move move : game.getHalfMoves()) {
                board.doMove(move);
                String from = move.getFrom().toString().toLowerCase();
                String to = move.getTo().toString().toLowerCase();
                String promo = (move.getPromotion() != null) ? move.getPromotion().getSanSymbol().toLowerCase() : "";
                String uci = from + to + promo;
                uciMoves.add(uci);
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Parsen von PGN: " + e.getMessage());
            e.printStackTrace();
        }

        return uciMoves;
    }

}
