package org.example.telegrambot;

public class Fighter {
    private String name;
    private String nickname;
    private String record;
    private String weightClass;

    public Fighter(String name, String nickname, String record, String weightClass) {
        this.name = name;
        this.nickname = nickname;
        this.record = record;
        this.weightClass = weightClass;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRecord() {
        return record;
    }

    public String getWeightClass() {
        return weightClass;
    }
}
