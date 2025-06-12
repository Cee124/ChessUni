package de.thm.informatik.chess.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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

        //Zielmap in der Eröffnungen gespeichert werden sollen
        Map<String, String> openingsMap = new LinkedHashMap<>();

        //Wählt alle b Elemente aus der html
        Elements openingNames = doc.select("b");

        //Iteration durch alle Eröffnungsnamen
        for (Element b : openingNames) {
            String name = b.text(); // value für die Map

            // Gehe zum nächsten Node nach <b>
            org.jsoup.nodes.Node next = b.nextSibling();

            while (next != null) {
                // Wenn es sich um einen TextNode handelt (direkt nach <b>), ist das die Zugfolge
                if (next instanceof org.jsoup.nodes.TextNode) {
                    String sanLine = ((org.jsoup.nodes.TextNode) next).text().trim();

                    // Prüfen, ob es eine gültige Zugfolge ist (z.B. mit 1. oder 1 e4)
                    if (sanLine.matches("^\\d+\\s.*") || sanLine.matches("^\\d+\\.?.*")) {
                        String uci = convertSanToUci(sanLine);
                        if (uci != null && !uci.isEmpty()) {
                            openingsMap.put(uci, name);
                        }
                        break; // fertig mit dieser Eröffnung
                    }
                }
                next = next.nextSibling(); // nächster Node (könnte <br> sein, ignorieren wir)
            }
        }
        return openingsMap;
    }

    private static String convertSanToUci(String sanMoves) {
        String cleaned = sanMoves.replaceAll("\\d+\\.", "").trim();
        String[] moves = cleaned.split("\\s+");
        System.out.println(cleaned);

        StringBuilder sb = new StringBuilder();
        for (String move : moves) {
            sb.append(move.toLowerCase());
        }
        return sb.toString();
    }
}
