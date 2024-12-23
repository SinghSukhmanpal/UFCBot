package org.example.telegrambot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScraper {

    public static List<Fighter> scrapeFighters(String url) throws IOException {
        List<Fighter> fighters = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        Elements fighterElements = doc.select(".fighter-card"); // Aggiorna il selettore secondo il sito
        for (Element fighterElement : fighterElements) {
            String name = fighterElement.select(".name").text();
            String nickname = fighterElement.select(".nickname").text();
            String record = fighterElement.select(".record").text();
            String weightClass = fighterElement.select(".weight-class").text();

            Fighter fighter = new Fighter(name, nickname, record, weightClass);
            fighters.add(fighter);
        }

        return fighters;
    }

    public static List<Event> scrapeEvents(String url) throws IOException {
        List<Event> events = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        Elements eventElements = doc.select(".event-card"); // Aggiorna il selettore secondo il sito
        for (Element eventElement : eventElements) {
            String eventName = eventElement.select(".event-name").text();
            String eventDate = eventElement.select(".event-date").text();
            String location = eventElement.select(".location").text();
            String mainFight = eventElement.select(".main-fight").text();

            Event event = new Event(eventName, eventDate, location, mainFight);
            events.add(event);
        }

        return events;
    }
}
