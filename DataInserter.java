package org.example.telegrambot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DataInserter {

    private final DatabaseManager databaseManager;

    public DataInserter() {
        this.databaseManager = new DatabaseManager();
    }

    public void insertFighters(List<Fighter> fighters) {
        String query = "INSERT INTO combattenti (nome, soprannome, record, categoria_peso) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (Fighter fighter : fighters) {
                stmt.setString(1, fighter.getName());
                stmt.setString(2, fighter.getNickname());
                stmt.setString(3, fighter.getRecord());
                stmt.setString(4, fighter.getWeightClass());
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Fighters inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertEvents(List<Event> events) {
        String query = "INSERT INTO eventi (nome_evento, data_evento, luogo, main_fight) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (Event event : events) {
                stmt.setString(1, event.getEventName());
                stmt.setString(2, event.getEventDate());
                stmt.setString(3, event.getLocation());
                stmt.setString(4, event.getMainFight());
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Events inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
