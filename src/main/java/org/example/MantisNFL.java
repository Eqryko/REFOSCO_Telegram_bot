package org.example;

import org.example.api.EspnClient;
import org.example.api.HighlightlyClient;
import org.example.db.DatabaseManager;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MantisNFL implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public MantisNFL(String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText().trim();
        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        long telegramId = update.getMessage().getFrom().getId();

        registerUser(telegramId, username);
        incrementStat(text);

        String response;

        switch (text) {

            case "/start" -> response = """
                    🏈 Benvenuto su MantisNFL!
                    
                    Comandi disponibili:
                    /results   - Ultimi risultati NFL
                    /standings - Classifica NFL
                    /teams     - Squadre NFL
                    /lastgames - Partite salvate
                    /stats     - Statistiche bot
                    /help
                    /highlights
                    """;

            case "/help" -> response = """
                    📌 Comandi:
                    
                    /results   - Ultimi risultati NFL
                    /standings - Classifica NFL
                    /teams     - Squadre NFL
                    /lastgames - Partite salvate
                    /stats     - Statistiche bot
                    /highlights
                    """;

            case "/results" -> response = EspnClient.getResults();
            case "/standings" -> response = EspnClient.getStandings();
            case "/teams" -> response = EspnClient.getTeams();


            default -> response = "❌ Comando non riconosciuto. Usa /help";
        }

        try {
            telegramClient.execute(
                    new SendMessage(String.valueOf(chatId), response)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- DATABASE ----------------

    private void registerUser(long telegramId, String username) {
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                     INSERT OR IGNORE INTO users
                     (telegram_id, username, first_seen, commands_used)
                     VALUES (?, ?, datetime('now'), 0)
                     """)) {

            ps.setLong(1, telegramId);
            ps.setString(2, username);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void incrementStat(String command) {
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                INSERT INTO stats (command, usage_count)
                VALUES (?, 1)
                ON CONFLICT(command)
                DO UPDATE SET usage_count = usage_count + 1
            """)) {

            ps.setString(1, command);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStats() {
        StringBuilder sb = new StringBuilder("📊 Statistiche bot\n\n");

        try (Connection c = DatabaseManager.getConnection();
             ResultSet rs = c.createStatement()
                     .executeQuery("SELECT * FROM stats")) {

            while (rs.next()) {
                sb.append(rs.getString("command"))
                        .append(": ")
                        .append(rs.getInt("usage_count"))
                        .append("\n");
            }

        } catch (Exception e) {
            return "❌ Errore lettura statistiche";
        }

        return sb.toString();
    }

    private String getLastGames() {
        StringBuilder sb = new StringBuilder("🏈 Ultime partite salvate\n\n");

        try (Connection c = DatabaseManager.getConnection();
             ResultSet rs = c.createStatement()
                     .executeQuery("""
                        SELECT * FROM matches
                        ORDER BY id DESC
                        LIMIT 5
                     """)) {

            while (rs.next()) {
                sb.append(rs.getString("home_team"))
                        .append(" ")
                        .append(rs.getInt("home_score"))
                        .append(" - ")
                        .append(rs.getInt("away_score"))
                        .append(" ")
                        .append(rs.getString("away_team"))
                        .append("\n");
            }

        } catch (Exception e) {
            return "⚠ Nessuna partita salvata";
        }

        return sb.toString();
    }
}
