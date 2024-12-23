package org.example.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class UFCBot extends TelegramLongPollingBot {

    private final DatabaseManager databaseManager;
    private final WebScraper webScraper;
    private final DataInserter dataInserter;

    public UFCBot(WebScraper webScraper, DataInserter dataInserter) {
        this.databaseManager = new DatabaseManager();
        this.webScraper = webScraper;
        this.dataInserter = dataInserter;
    }

    @Override
    public String getBotUsername() {
        return "mmaupdates_bot";
    }

    @Override
    public String getBotToken() {
        return "7610060197:AAHwLcMu6uoPI0Nm4Oqxd7TSIggiTdmdUBs";
    }

    @Override
    public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/start")) {
                sendMessage(chatId, "Benvenuto su MMA Updates Bot! Puoi cercare informazioni sui combattenti, eventi e altro.");
            } else if (messageText.startsWith("/cerca")) {
                String fighterName = messageText.replace("/cerca", "").trim();
                if (!fighterName.isEmpty()) {
                    String fighterInfo = getFighterInfoWithFallback(fighterName);
                    sendMessage(chatId, fighterInfo);
                } else {
                    sendMessage(chatId, "Per favore, specifica il nome di un combattente.");
                }
            } else if (messageText.startsWith("/evento")) {
                String eventNumber = messageText.replace("/evento", "").trim();
                if (!eventNumber.isEmpty()) {
                    try {
                        int numeroEvento = Integer.parseInt(eventNumber);
                        String eventInfo = getEventInfoWithFallback(numeroEvento);
                        sendMessage(chatId, eventInfo);
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "Numero evento non valido. Per favore inserisci un numero valido.");
                    }
                } else {
                    sendMessage(chatId, "Per favore, specifica il numero dell'evento.");
                }
            } else if (messageText.startsWith("/classifica")) {
                String categoryName = messageText.replace("/classifica", "").trim();
                if (!categoryName.isEmpty()) {
                    String rankings = databaseManager.getRankingsByCategory(categoryName);
                    sendMessage(chatId, rankings);
                } else {
                    sendMessage(chatId, "Per favore, specifica il nome della categoria (ad esempio, '/classifica Pesi Massimi').");
                }
            } else if (messageText.startsWith("/aiuto")) {
                sendMessage(chatId, "Comandi disponibili:\n/start - Inizia la conversazione\n/cerca [combattente] - Cerca info su un combattente\n/evento [numero evento] - Info su un evento\n/classifica [categoria] - Mostra la classifica per una categoria\n/aiuto - Mostra i comandi disponibili");
            } else if (messageText.startsWith("/aggiorna")) {
                String updateStatus = databaseManager.updateDatabase();
                sendMessage(chatId, updateStatus);
            } else {
                sendMessage(chatId, "Comando non riconosciuto. Usa /aiuto per vedere i comandi disponibili.");
            }
        }
    }


    private void sendMessage(long chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getFighterInfoWithFallback(String name) {
        String fighterInfo = databaseManager.getFighterInfo(name);
        if (fighterInfo == null || fighterInfo.isEmpty()) {
            try {
                List<Fighter> fighters = webScraper.scrapeFighters("https://www.ufc.com/search?query=" + name.replace(" ", "%20"));
                Fighter foundFighter = fighters.stream()
                        .filter(f -> f.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
                if (foundFighter != null) {
                    dataInserter.insertFighters(List.of(foundFighter));
                    return String.format("Nome: %s\nSoprannome: %s\nRecord: %s\nCategoria Peso: %s\n",
                            foundFighter.getName(),
                            foundFighter.getNickname(),
                            foundFighter.getRecord(),
                            foundFighter.getWeightClass());
                } else {
                    return "Combattente non trovato nemmeno online.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Errore nel cercare informazioni sul combattente.";
            }
        }
        return fighterInfo;
    }

    private String getEventInfoWithFallback(int eventNumber) {
        String eventInfo = databaseManager.getEventInfo(eventNumber);
        if (eventInfo == null || eventInfo.isEmpty()) {
            try {
                List<Event> events = webScraper.scrapeEvents("https://www.ufc.com/events?query=" + eventNumber);
                Event foundEvent = events.stream()
                        .filter(e -> e.getEventName().contains(String.valueOf(eventNumber)))
                        .findFirst()
                        .orElse(null);
                if (foundEvent != null) {
                    dataInserter.insertEvents(List.of(foundEvent));
                    return String.format("Evento: %s\nData: %s\nLuogo: %s\nMain Fight: %s\n",
                            foundEvent.getEventName(),
                            foundEvent.getEventDate(),
                            foundEvent.getLocation(),
                            foundEvent.getMainFight());
                } else {
                    return "Evento non trovato nemmeno online.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Errore nel cercare informazioni sull'evento.";
            }
        }
        return eventInfo;
    }

    public void setMyCommands(List<BotCommand> commands, BotCommandScope scope, String languageCode) {
        try {
            execute(new SetMyCommands(commands, scope, languageCode));
        } catch (TelegramApiException e) {
            System.err.println("Errore durante l'impostazione dei comandi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
