package org.example;

import org.example.api.EspnClient;
import org.example.db.DatabaseManager;
import org.example.api.TeamData;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.InputFile;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;


public class MantisNFL implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public MantisNFL(String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }
    private final Map<Long, String> userState = new HashMap<>();


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
        // Gestione input successivo
        if (userState.containsKey(chatId)) {

            String state = userState.get(chatId);

            if (state.equals("WAITING_TEAM_ID")) {

                if (!text.matches("\\d+")) {
                    response = "❌ Inserisci un ID valido (numero)";
                } else {

                    int teamId = Integer.parseInt(text);
                    TeamData team = EspnClient.getTeamById(teamId);

                    if (team == null) {
                        response = "❌ Team non trovato";
                    } else {

                        // Invia logo se presente
                        if (team.logoUrl != null && !team.logoUrl.equals("N/A")) {
                            try {
                                SendPhoto photo = new SendPhoto(
                                        String.valueOf(chatId),
                                        new InputFile(team.logoUrl)
                                );
                                telegramClient.execute(photo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        // Testo squadra
                        response = team.text;
                    }

                    userState.remove(chatId);
                }

                try {
                    SendMessage sm = new SendMessage(
                            String.valueOf(chatId),
                            response
                    );
                    sm.setParseMode("Markdown");
                    telegramClient.execute(
                            sm
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            if(state.equals("WAITING_TEAM_ROSTER")) {
                if (!text.matches("\\d+")) {
                    response = "❌ Inserisci un ID valido (numero)";
                } else {
                    int teamId = Integer.parseInt(text);
                    response = EspnClient.Roster(teamId);
                    userState.remove(chatId);
                }

                try {
                    SendMessage sm = new SendMessage(
                            String.valueOf(chatId),
                            response
                    );
                    sm.setParseMode("Markdown");
                    telegramClient.execute(
                            sm
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }


        switch (text) {

            // ---------------- START ----------------

            case "/start" -> response = """
                    🏈 *MantisNFL Bot*
                    
                    Benvenuto *%s*!  
                    Qui puoi seguire la **NFL in tempo reale** 📊
                    
                    🔥 *Cosa puoi fare*
                    • Risultati e partite
                    • Classifiche ufficiali
                    • Squadre e giocatori
                    • News e statistiche
                    • Storico utilizzo bot
                    
                    📌 *Comandi principali*
                    /results   – Risultati e prossime partite
                    /teams     – Squadre NFL  
                    /team      - Singola squadra
                    /news      – Ultime news  
                    /roster   – Giocatori  
                    /nextgames     – Prossime partite 
                    /save      – Partite salvate  
                    /stats     – Statistiche bot  
                    /help      – Aiuto
                    
                    🐜 *Powered by ESPN API*
                    """.formatted(username != null ? username : "utente");

            // ---------------- HELP ----------------

            case "/help" -> response = """
                    📖 *Guida comandi*
                    
                    🏈 Partite
                    /results   – Risultati e match futuri
                    /nextgames     – Prossime partite
                    
                    📰 Info
                    /teams     – Squadre NFL
                    /team      - Singola squadra
                    /roster   – Giocatori di una squadra
                    /news      – Ultime news ESPN
                    
                    📊 Bot
                    /save – Partite salvate
                    /stats     – Statistiche utilizzo
                    """;

            // ---------------- ESPN API ----------------

            case "/results" -> response = EspnClient.getResults();
            case "/standings" -> response = EspnClient.getStandings();
            case "/teams" -> response = EspnClient.getTeams();
            case "/team" -> {
                userState.put(chatId, "WAITING_TEAM_ID");
                response = """
            🏈 Team NFL
            
            🔢 Inserisci l'ID del team
            Esempio: 12 = Kansas City Chiefs
            """;
            }

            case "/news" -> response = EspnClient.getNews();
            case "/roster" -> {
                userState.put(chatId, "WAITING_TEAM_ROSTER");
                response = """
                        🏈 *Team NFL*
                        
                        🔢 Inserisci l'ID del team
                        Esempio: 12 = Kansas City Chiefs
                        """;
            }
            //case "/leaders" -> response = EspnClient.getLeaders();
            case "/nextgames" -> response = EspnClient.getTodayGames();

            // ---------------- DATABASE ----------------

            case "/stats" -> response = getStats();
            case "/save" -> response = getLastGames();

            default -> response = "❌ Comando non riconosciuto. Usa /help";
        }

        try {
            SendMessage sm = new SendMessage(
                    String.valueOf(chatId),
                    response
            );
            sm.setParseMode("Markdown");
            telegramClient.execute(
                    sm
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
        StringBuilder sb = new StringBuilder("📊 *Statistiche bot*\n\n");

        try (Connection c = DatabaseManager.getConnection();
             ResultSet rs = c.createStatement()
                     .executeQuery("SELECT * FROM stats")) {

            while (rs.next()) {
                sb.append("• ")
                        .append(rs.getString("command"))
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
        StringBuilder sb = new StringBuilder("🏈 *Ultime partite salvate*\n\n");

        try (Connection c = DatabaseManager.getConnection();
             ResultSet rs = c.createStatement()
                     .executeQuery("""
                        SELECT * FROM matches
                        ORDER BY id DESC
                        LIMIT 5
                     """)) {

            while (rs.next()) {
                sb.append("• ")
                        .append(rs.getString("home_team"))
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
