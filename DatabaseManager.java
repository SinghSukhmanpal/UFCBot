package org.example.telegrambot;

import java.sql.*;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ufc_bot";
    private static final String DB_USER = "ufc_user";
    private static final String DB_PASSWORD = "password123";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Errore nella connessione al database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Errore nella chiusura della connessione: " + e.getMessage());
            }
        }
    }

    public String getFighterInfo(String nome) {
        String query = "SELECT * FROM combattenti WHERE nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("Nome: %s\nSoprannome: %s\nRecord: %s\nCategoria Peso: %s\nSpecialità: %s\n",
                        rs.getString("nome"),
                        rs.getString("soprannome"),
                        rs.getString("record"),
                        rs.getString("categoria_peso"),
                        rs.getString("specialita"));
            } else {
                return "Combattente non trovato.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore nel recuperare i dati del combattente.";
        }
    }

    public String getEventInfo(int numeroEvento) {
        String query = "SELECT * FROM eventi WHERE numero_evento = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, numeroEvento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("Evento: %s\nData: %s\nLuogo: %s\nMain Fight: %s\n",
                        rs.getString("nome_evento"),
                        rs.getDate("data_evento"),
                        rs.getString("luogo"),
                        rs.getString("main_fight"));
            } else {
                return "Evento non trovato.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore nel recuperare i dati dell'evento.";
        }
    }

    public String getRankingsByCategory(String category) {
        String query = "SELECT nome, rank FROM classifiche c JOIN combattenti cb ON c.combattente_id = cb.id WHERE categoria_peso = ? ORDER BY rank";
        StringBuilder rankings = new StringBuilder("Classifica UFC - Categoria: " + category + "\n");

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                return "Nessuna classifica trovata per la categoria: " + category;
            }

            while (rs.next()) {
                rankings.append(String.format("Rank %d: %s\n", rs.getInt("rank"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore nel recuperare la classifica per la categoria.";
        }

        return rankings.toString();
    }


    public String updateDatabase() {
        try {

            List<Fighter> fighters = WebScraper.scrapeFighters("https://www.ufc.com/fighters");
            List<Event> events = WebScraper.scrapeEvents("https://www.ufc.com/events");

            DataInserter dataInserter = null;
            dataInserter.insertFighters(fighters);
            dataInserter.insertEvents(events);

            return "Database aggiornato con successo!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore nell'aggiornamento del database: " + e.getMessage();
        }
    }
}
