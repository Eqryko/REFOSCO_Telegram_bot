package org.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Properties;

public class HighlightlyClient {
    Properties props = new Properties();
    //props.load(new FileInputStream("config.properties"));
    String apiKey = props.getProperty("highlightly.api.key");

    private static final String API_KEY = "26dbb245-ac48-4af1-8006-a7053f606a6b";
    private static final String BASE_URL =
            "https://american-football.highlightly.net";

    private static final OkHttpClient client = new OkHttpClient();

    public static String getLatestResults() {

        String url = BASE_URL + "/matches?league=NFL";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("HTTP CODE: " + response.code());

            if (!response.isSuccessful()) {
                return "Errore API Highlightly: " + response.code();
            }

            return response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
            return "Errore di connessione Highlightly";
        }
    }
    public static String getStandings() {

        String url = BASE_URL + "/standings?league=NFL";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return "Errore API: " + response.code();
            }

            return response.body().string();

        } catch (IOException e) {
            return "Errore di connessione API";
        }
    }
    private static String parseResults(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            StringBuilder sb = new StringBuilder("🏈 NFL – Ultime partite\n\n");

            for (JsonNode match : root.get("matches")) {
                String home = match.get("homeTeam").get("name").asText();
                String away = match.get("awayTeam").get("name").asText();
                int homeScore = match.get("homeTeam").get("score").asInt();
                int awayScore = match.get("awayTeam").get("score").asInt();
                String status = match.get("status").asText();

                sb.append(home)
                        .append(" ")
                        .append(homeScore)
                        .append(" - ")
                        .append(awayScore)
                        .append(" ")
                        .append(away)
                        .append(" (")
                        .append(status)
                        .append(")\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "Errore parsing dati NFL";
        }
    }

}