package org.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.example.model.MatchResult;

import java.io.IOException;
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

}
