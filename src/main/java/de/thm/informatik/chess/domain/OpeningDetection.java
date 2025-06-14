package de.thm.informatik.chess.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class OpeningDetection {

    //Methode zur Umwandlung der Eröffnungen in Map Objekte
    public Map<String, String> loadOpenings(String htmlPath) throws IOException {
        //Läd Datei aus Klassenpfad, wenn nicht gefunden wir Exception geworfen
        InputStream is = getClass().getResourceAsStream(htmlPath);
        if (is == null) {
            throw new IOException("Resource nicht gefunden: " + htmlPath);
        }
        //Liest als UTF 8 und parst mit Jsoup
        String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(html);

        //Zielmap in der Eröffnungen gespeichert werden sollen, Reihenfolge bleibt erhalten (Linked)
        Map<String, String> openingsMap = new LinkedHashMap<>();

        //Wählt alle b Elemente aus der html
        List<Element> openingNames = doc.select("b");

        //Iteration durch alle Eröffnungsnamen
        for (Element b : openingNames) {
            String name = b.text(); // value für die Map

            //Gehe zum nächsten Node nach <b>
            org.jsoup.nodes.Node next = b.nextSibling();

            //Solange es weitere Nodes gibt
            while (next != null) {
                // Wenn es sich um einen TextNode handelt (direkt nach <b>), ist das die Zugfolge
                if (next instanceof org.jsoup.nodes.TextNode) {
                    //Zugfolge in Variable als Text speichern
                    String sanLine = ((org.jsoup.nodes.TextNode) next).text().trim();

                    // Prüfen, ob es eine gültige Zugfolge ist (z.B. mit 1. oder 1 e4)
                    if (sanLine.matches("^\\d+\\s.*") || sanLine.matches("^\\d+\\.?.*")) {
                        //Zugfolge zu gültigem Uci Format umwandeln
                        String uci = convertSanToUci(sanLine);
                        //Wenn eine Zugfolge vorhanden ist wird sie als key und der name als value in der Map gespeichert
                        if (uci != null && !uci.isEmpty()) {
                            openingsMap.put(uci, name);
                        }
                        break; // fertig mit dieser Eröffnung
                    }
                }
                next = next.nextSibling(); //nächster Node
            }
        }
        return openingsMap;
    }

    private static String convertSanToUci(String sanMoves) {
        //entfernt alle Zugnummer wie z.B. 1 e4 oder 1. e4
        String cleaned = sanMoves.replaceAll("\\d+\\.", "").trim();
        //trennt moves anhand von Leerzeichen
        String[] moves = cleaned.split("\\s+");

        StringBuilder sb = new StringBuilder();
        //Iteration mit for-each durch alle moves
        for (String move : moves) {
            //moves ohne Trennzeichen dem StringBuilder hinzufügen
            sb.append(move.toLowerCase());
        }
        return sb.toString();
    }
}
