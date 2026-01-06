/*
__________        _____                           ___________            .__
\______   \ _____/ ____\____  ______ ____  ____   \_   _____/ ___________|__| ____  ____
 |       _// __ \   __\/  _ \/  ___// ___\/  _ \   |    __)_ /    \_  __ \  |/ ___\/  _ \
 |    |   \  ___/|  | (  <_> )___ \\  \__(  <_> )  |        \   |  \  | \/  \  \__(  <_> )
 |____|_  /\___  >__|  \____/____  >\___  >____/  /_______  /___|  /__|  |__|\___  >____/
        \/     \/                \/     \/                \/     \/              \/
        Classe 5BII
        TPSIT
        Progetto Telegram Bot
*/

package org.example;

import org.example.api.HighlightlyClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

public class MantisNFL implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public MantisNFL(String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        String response;

        switch (text) {
            case "/start":
                response = "🏈 Benvenuto su MantisNFL!\n" +
                        "/results - risultati NFL\n" +
                        "/standings - classifica";
                break;
            case "/results":
                response = HighlightlyClient.getLatestResults();
                break;
            case "/standings":
                response = HighlightlyClient.getStandings();
                break;
            default:
                response = "❌ Comando non riconosciuto";
        }
        if (response.length() > 4000) {
            response = response.substring(0, 4000) + "\n\n⚠️ Output troncato";
        }
        SendMessage msg = new SendMessage(chatId.toString(), response);

        try {
            telegramClient.execute(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
