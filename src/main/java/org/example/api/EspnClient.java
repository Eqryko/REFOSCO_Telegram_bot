package org.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class EspnClient {

    private static final String BASE_URL =
            "https://site.api.espn.com/apis/site/v2/sports/football/nfl";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // ---------------- RESULTS / NEXT GAMES ----------------

    public static String getResults() {

        String url = BASE_URL + "/scoreboard";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore ESPN: " + response.code();
            }

            JsonNode root = mapper.readTree(response.body().string());
            JsonNode events = root.path("events");

            StringBuilder sb = new StringBuilder("🏈 NFL – Partite\n\n");

            int count = 0;
            for (JsonNode event : events) {
                if (count++ == 5) break;

                String name = event.path("name").asText("Match");
                String date = event.path("date").asText("");

                JsonNode competitors =
                        event.path("competitions").get(0).path("competitors");

                String home = competitors.get(0).path("team").path("displayName").asText();
                String away = competitors.get(1).path("team").path("displayName").asText();

                String homeScore = competitors.get(0).path("score").asText("-");
                String awayScore = competitors.get(1).path("score").asText("-");

                sb.append(home).append(" ")
                        .append(homeScore).append(" - ")
                        .append(awayScore).append(" ")
                        .append(away).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore connessione ESPN";
        }
    }

    // ---------------- STANDINGS ----------------

    public static String getStandings() {

        String url = BASE_URL + "/standings";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore ESPN: " + response.code();
            }

            JsonNode root = mapper.readTree(response.body().string());
            JsonNode entries = root
                    .path("children").get(0)
                    .path("standings").path("entries");

            StringBuilder sb = new StringBuilder("🏆 NFL – Standings\n\n");

            int pos = 1;
            for (JsonNode team : entries) {
                if (pos > 10) break;

                String name = team.path("team").path("displayName").asText();
                int wins = team.path("stats").get(0).path("value").asInt();
                int losses = team.path("stats").get(1).path("value").asInt();

                sb.append(pos++).append(". ")
                        .append(name)
                        .append(" (")
                        .append(wins).append("-")
                        .append(losses).append(")\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore parsing standings";
        }
    }

    // ---------------- TEAMS ----------------

    public static String getTeams() {

        String url = BASE_URL + "/teams";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore ESPN: " + response.code();
            }

            JsonNode root = mapper.readTree(response.body().string());
            JsonNode teams = root.path("sports").get(0)
                    .path("leagues").get(0)
                    .path("teams");

            StringBuilder sb = new StringBuilder("🏈 Squadre NFL\n\n");

            for (JsonNode t : teams) {
                sb.append("• ")
                        .append(t.path("team").path("displayName").asText())
                        .append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore caricamento squadre";
        }
    }
}
