package app;

import dto.Update;
import telegram.TelegramBotApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String token = System.getenv("BOT_TOKEN");
        Path audioFolder = Path.of("audios");
        Path audioFile = audioFolder.resolve("audio.oga");
        Path jsonFile = audioFolder.resolve("audio.json");
        TelegramBotApi api = new TelegramBotApi(token);

        Files.createDirectories(audioFolder);

        final int[] updateId = new int[1];
        updateId[0] = 0;

        while (true) {
            api.getUpdates(updateId[0] + 1).forEach(n -> {
                try {
                    updateId[0] = processMessage(api, n, audioFile, jsonFile);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static int processMessage(TelegramBotApi api, Update n, Path audioFile, Path jsonFile) throws IOException, InterruptedException {

        if (n.message().voice() != null) {

            api.downloadAudio(n.message().voice().file_id(), audioFile);

            WhisperApi whisper = new WhisperApi();

            String t = whisper.transcribe(audioFile, jsonFile);

            api.sendMessage(t, String.valueOf(n.message().chat().id()), n.message().message_id());

        } else {
            String message = "Send me an audio and I will transcribe it \uD83D\uDC4D";
            api.sendMessage(message, String.valueOf(n.message().chat().id()));
        }
        return n.update_id();
    }
}
