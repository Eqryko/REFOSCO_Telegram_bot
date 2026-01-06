package org.example.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HighlightlyClient {

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

}