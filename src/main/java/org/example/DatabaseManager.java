package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {

            // tabella utenti
            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    telegram_id INTEGER UNIQUE,
                    username TEXT,
                    first_seen TEXT,
                    commands_used INTEGER
                )
            """);

            // tabella partite
            s.execute("""
                CREATE TABLE IF NOT EXISTS matches (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    home_team TEXT,
                    away_team TEXT,
                    home_score INTEGER,
                    away_score INTEGER,
                    status TEXT,
                    date TEXT
                )
            """);

            // statistiche comandi
            s.execute("""
                CREATE TABLE IF NOT EXISTS stats (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    command TEXT UNIQUE,
                    usage_count INTEGER
                )
            """);

            System.out.println("Database inizializzato");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
