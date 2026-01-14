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
            JsonNode groups = root.path("children");

            StringBuilder sb = new StringBuilder("🏆 NFL – Standings\n\n");

            for (JsonNode group : groups) {

                String groupName = group.path("name").asText();
                sb.append("📌 ").append(groupName).append("\n");

                JsonNode entries =
                        group.path("standings").path("entries");

                int pos = 1;
                for (JsonNode team : entries) {
                    if (pos > 4) break;

                    String name = team.path("team").path("displayName").asText();
                    int wins = team.path("stats").get(0).path("value").asInt();
                    int losses = team.path("stats").get(1).path("value").asInt();

                    sb.append(pos++)
                            .append(". ")
                            .append(name)
                            .append(" (")
                            .append(wins).append("-")
                            .append(losses).append(")\n");
                }
                sb.append("\n");
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
                        .append(t.path("team").path("abbreviation").asText()).append(" ")
                        .append(t.path("team").path("displayName").asText())
                        .append(" (ID: ").append(t.path("team").path("id").asText())
                        .append(")\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore caricamento squadre";
        }
    }
    public static String getNews() {

        String url = BASE_URL + "/news";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful())
                return "❌ Errore ESPN: " + response.code();

            JsonNode articles = mapper
                    .readTree(response.body().string())
                    .path("articles");

            StringBuilder sb = new StringBuilder("📰 Ultime NFL News ESPN\n\n");

            int count = 0;
            for (JsonNode a : articles) {
                if (count++ == 5) break;

                sb.append("• ")
                        .append(a.path("headline").asText())
                        .append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore caricamento news";
        }
    }
    public static String Roster(int id) {

        String url = BASE_URL + "/teams/" + id + "/roster";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Errore ESPN: " + response.code();
            }

            JsonNode root = mapper.readTree(response.body().string());
            JsonNode athletes = root.path("athletes");

            StringBuilder sb = new StringBuilder("👥 Giocatori NFL (ID squadra: " + id + ")\n\n");

            int count = 0;
            for (JsonNode group : athletes) {
                for (JsonNode p : group.path("items")) {
                    if (count++ == 5) break;

                    sb.append("• ")
                            .append(p.path("fullName").asText())
                            .append(" – ")
                            .append(p.path("position").path("abbreviation").asText())
                            .append("\n");
                }
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Errore caricamento giocatori";
        }
    }

    public static String getTodayGames() {

        String url = BASE_URL + "/scoreboard";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful())
                return "❌ Errore ESPN";

            JsonNode events = mapper
                    .readTree(response.body().string())
                    .path("events");

            StringBuilder sb = new StringBuilder("📅 Prossime partite\n\n");

            int count = 0;
            for (JsonNode e : events) {
                if (count++ == 3) break;
                sb.append("• ").append(e.path("name").asText()).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "❌ Nessuna partita oggi";
        }
    }
    public static String getTeamById(int teamId) {

        String url = BASE_URL + "/teams/" + teamId + "?enable=venues";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "❌ Team non trovato";
            }

            JsonNode root = mapper.readTree(response.body().string());
            JsonNode team = root.path("team");

            String name = team.path("displayName").asText("N/A");
            String abbrev = team.path("abbreviation").asText("N/A");
            String color = team.path("color").asText("N/A");

            String venue = "N/A";
            JsonNode venues = root.path("venues");
            if (venues.isArray() && venues.size() > 0) {
                venue = venues.get(0).path("fullName").asText("N/A");
            }

            return """
                🏈 %s (%s)
                
                🏟 Stadio: %s
                🎨 Colore: #%s
                🆔 Team ID: %d
                """.formatted(name, abbrev, venue, color, teamId);

        } catch (Exception e) {
            return "❌ Errore caricamento team";
        }
    }
}