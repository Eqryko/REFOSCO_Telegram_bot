package org.example;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.example.db.DatabaseManager;

/*
__________        _____                           ___________            .__
\______   \ _____/ ____\____  ______ ____  ____   \_   _____/ ___________|__| ____  ____
 |       _// __ \   __\/  _ \/  ___// ___\/  _ \   |    __)_ /    \_  __ \  |/ ___\/  _ \
 |    |   \  ___/|  | (  <_> )___ \\  \__(  <_> )  |        \   |  \  | \/  \  \__(  <_> )
 |____|_  /\___  >__|  \____/____  >\___  >____/  /_______  /___|  /__|  |__|\___  >____/
        \/     \/                \/     \/                \/     \/              \/
        Classe 5BII - TPSIT
*/

public class Main {
    public static void main(String[] args) {

        String botToken = "8575183641:AAE3fnW-3pvWNm--6FxdpIETNGVHero3kaI";

        try {
            DatabaseManager.init();

            TelegramBotsLongPollingApplication app =
                    new TelegramBotsLongPollingApplication();

            app.registerBot(botToken, new MantisNFL(botToken));

            System.out.println("!!! Bot avviato correttamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
