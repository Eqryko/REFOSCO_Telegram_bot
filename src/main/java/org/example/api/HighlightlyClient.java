package org.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.example.db.DatabaseManager;
import org.example.model.MatchResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;



public class HighlightlyClient {

    private static final String API_KEY = "";
    private static final String BASE_URL = "https://american-football.highlightly.net";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getLatestResults() {

        String url = BASE_URL + "/matches?league=NFL";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore API: " + response.code();
            }

            String json = response.body().string();
            return parseMatches(json);

        } catch (IOException e) {
            return "❌ Errore di connessione all'API";
        }
    }

    private static String parseMatches(String json) throws IOException {

        JsonNode root = mapper.readTree(json);
        JsonNode data = root.path("data");

        StringBuilder sb = new StringBuilder("🏈 NFL – Ultimi risultati\n\n");

        int count = 0;
        for (JsonNode match : data) {
            if (count++ == 5) break;

            String home = match.path("homeTeam").path("name").asText("Home");
            String away = match.path("awayTeam").path("name").asText("Away");

            JsonNode score = match.path("score");
            int homeScore = score.path("home").asInt(0);
            int awayScore = score.path("away").asInt(0);

            sb.append(home)
                    .append(" ")
                    .append(homeScore)
                    .append(" - ")
                    .append(awayScore)
                    .append(" ")
                    .append(away)
                    .append("\n");
        }

        return sb.toString();
    }
    public static String getStandings() {

        String url = BASE_URL + "/standings?league=NFL";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore API: " + response.code();
            }

            String json = response.body().string();
            return parseStandings(json);

        } catch (IOException e) {
            return "❌ Errore di connessione API";
        }
    }
    private static String parseStandings(String json) throws IOException {

        JsonNode root = mapper.readTree(json);
        JsonNode conferences = root.path("data");

        StringBuilder sb = new StringBuilder("🏆 NFL – Standings\n\n");

        for (JsonNode conference : conferences) {

            String confName = conference.path("conference").asText("Conference");
            sb.append(confName).append("\n");

            JsonNode teams = conference.path("teams");

            int position = 1;
            for (JsonNode team : teams) {
                if (position > 5) break; // max 5 squadre

                String name = team.path("team").path("name").asText("Team");
                int wins = team.path("wins").asInt(0);
                int losses = team.path("losses").asInt(0);

                sb.append(position)
                        .append(". ")
                        .append(name)
                        .append(" (")
                        .append(wins)
                        .append("-")
                        .append(losses)
                        .append(")\n");

                position++;
            }

            sb.append("\n");
        }

        return sb.toString();
    }
    public static String getTeams() {

        try {
            String url = "https://nfl-ncaa-highlights-api.p.rapidapi.com/teams?league=NFL&limit=32";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", API_KEY)
                    .header("x-rapidapi-host", "nfl-ncaa-highlights-api.p.rapidapi.com")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "❌ Errore API: " + response.statusCode();
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode teams = root.path("data");

            StringBuilder sb = new StringBuilder("🏈 Squadre NFL\n\n");

            for (JsonNode team : teams) {
                String name = team.path("displayName").asText();
                String abbr = team.path("abbreviation").asText();
                sb.append(abbr).append(" - ").append(name).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore caricamento squadre";
        }
    }
    public static String getHighlights() {

        String url = BASE_URL + "/highlights?league=NFL&limit=5";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore API: " + response.code();
            }

            String json = response.body().string();
            return parseHighlights(json);

        } catch (IOException e) {
            return "❌ Errore connessione Highlights";
        }
    }
    private static String parseHighlights(String json) throws IOException {

        JsonNode root = mapper.readTree(json);
        JsonNode data = root.path("data");

        StringBuilder sb = new StringBuilder("🎬 NFL – Highlights recenti\n\n");

        int count = 0;

        for (JsonNode h : data) {
            if (count++ == 5) break;

            String title = h.path("title").asText("Highlight NFL");
            String source = h.path("source").asText("Fonte sconosciuta");
            String url = h.path("url").asText("");

            sb.append("🏈 ").append(title).append("\n")
                    .append("📺 ").append(source).append("\n");

            if (!url.isEmpty()) {
                sb.append("🔗 ").append(url).append("\n");
            }

            sb.append("\n");
        }

        if (count == 0) {
            return "Nessun highlight disponibile al momento.";
        }

        return sb.toString();
    }


}
