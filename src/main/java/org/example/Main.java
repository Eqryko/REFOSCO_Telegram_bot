package org.example;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {

        String botToken = "8575183641:AAE3fnW-3pvWNm--6FxdpIETNGVHero3kaI";

        try {
            TelegramBotsLongPollingApplication app =
                    new TelegramBotsLongPollingApplication();

            app.registerBot(botToken, new MantisNFL(botToken));

            System.out.println("🤖 Bot avviato correttamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
