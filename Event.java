package org.example.telegrambot;

public class Event {
    private String eventName;
    private String eventDate;
    private String location;
    private String mainFight;

    public Event(String eventName, String eventDate, String location, String mainFight) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.location = location;
        this.mainFight = mainFight;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public String getMainFight() {
        return mainFight;
    }
}
