package org.example.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class UFCBotMain {
    public static void main(String[] args) {
        try {

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            UFCBot bot = new UFCBot(new WebScraper(), new DataInserter());
            botsApi.registerBot(bot);

            List<BotCommand> commands = new ArrayList<>();
            commands.add(new BotCommand("/start", "Inizia la conversazione con il bot"));
            commands.add(new BotCommand("/cerca", "Cerca informazioni su un combattente"));
            commands.add(new BotCommand("/evento", "Ottieni dettagli di un evento UFC"));
            commands.add(new BotCommand("/classifica", "Visualizza la classifica attuale"));
            commands.add(new BotCommand("/aggiorna", "Aggiorna manualmente il database"));
            commands.add(new BotCommand("/aiuto", "Visualizza i comandi disponibili"));

            bot.setMyCommands(commands, new BotCommandScopeDefault(), null);

            System.out.println("UFCBot è stato avviato con successo!");
        } catch (TelegramApiException e) {
            System.err.println("Errore durante l'avvio del bot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
