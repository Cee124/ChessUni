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

    public Map<String, String> loadOpenings(String htmlPath) throws IOException {
        //Resource als Stream laden
        InputStream is = getClass().getResourceAsStream(htmlPath);
        if (is == null) {
            throw new IOException("Resource nicht gefunden: " + htmlPath);
        }

        //InputStream in String umwandeln
        String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(html);

        //Ziel-Map
        Map<String, String> openingsMap = new LinkedHashMap<>();

        //Gehe durch alle <b> Elemente mit dem ECO-Namen
        Elements boldElements = doc.select("b");

        for (Element b : boldElements) {
            String name = b.text();

            //Gehe durch die nachfolgenden Siblings, um SAN-Züge zu finden
            Element current = b.nextElementSibling();
            while (current != null && current.tagName().equals("br")) {
                if (current.nextSibling() != null) {
                    String sanLine = current.nextSibling().toString().trim();

                    if (sanLine.matches("^\\d+\\..*")) {
                        String uci = convertSanToUci(sanLine);
                        if (uci != null && !uci.isEmpty()) {
                            openingsMap.put(uci, name);
                        }
                    }
                }
                current = current.nextElementSibling();
            }
        }
        return openingsMap;
    }

    private static String convertSanToUci(String sanMoves) {
        String cleaned = sanMoves.replaceAll("\\d+\\.", "").trim();
        String[] moves = cleaned.split("\\s+");

        StringBuilder sb = new StringBuilder();
        for (String move : moves) {
            sb.append(move.toLowerCase());
        }
        return sb.toString();
    }
}
