package app;

import dto.Update;
import telegram.TelegramBotApi;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String token = System.getenv("BOT_TOKEN");

        TelegramBotApi api = new TelegramBotApi(token);

        final int[] updateId = new int[1];
        updateId[0] = 0;

        api.getUpdates(updateId[0]).forEach(n -> {
            try {
                updateId[0] = processMessage(api, n);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        while(true){
            api.getUpdates(updateId[0]+1).forEach(n -> {
                try {
                    updateId[0] = processMessage(api, n);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static int processMessage(TelegramBotApi api, Update n) throws IOException, InterruptedException {

        if(n.message().text() != null){
            api.sendMessage(n.message().text(), String.valueOf(n.message().chat().id()));
        }



        if(n.message().voice() != null){


            Path path = api.downloadAudio(n.message().voice().file_id());

            WhisperApi whisper = new WhisperApi();

            String t = whisper.transcribe(path);

            api.sendMessage(t, String.valueOf(n.message().chat().id()));

        }

        return n.update_id();
    }
}
