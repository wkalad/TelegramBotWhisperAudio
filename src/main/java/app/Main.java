package app;

import app.DAO.UserDAO;
import dto.Update;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import telegram.TelegramBotApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String token = System.getenv("BOT_TOKEN");
        Path audioFolder = Path.of("audios");
        Path dataFolder = Path.of("data");
        Path audioFile = audioFolder.resolve("audio.oga");
        Path jsonFile = audioFolder.resolve("audio.json");
        TelegramBotApi api = new TelegramBotApi(token);

        Files.createDirectories(audioFolder);
        Files.createDirectories(dataFolder);

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        UserDAO userDAO = context.getBean(UserDAO.class);

        final int[] updateId = new int[1];
        updateId[0] = 0;

        while (true) {
            api.getUpdates(updateId[0] + 1).forEach(n -> {
                try {
                    updateId[0] = processMessage(api, n, audioFile, jsonFile, userDAO);
                } catch (Throwable t) {
                    System.err.println("Process Message Error: " + t);
                }
            });
        }
    }

    private static int processMessage(TelegramBotApi api, Update n, Path audioFile, Path jsonFile, UserDAO userDAO) throws IOException, InterruptedException {

        if (n.message() == null){
            return n.update_id();
        }

        if (n.message().voice() != null) {

            userDAO.saveOrUpdateUser(n.message().chat().id(), n.message().from().language_code(), "small");

            String language = userDAO.findLanguageByUser(n.message().chat().id()).get();
            String model = userDAO.findModelByUser(n.message().chat().id()).get();

            api.downloadAudio(n.message().voice().file_id(), audioFile);

            WhisperApi whisper = new WhisperApi();

            String t = whisper.transcribe(audioFile, jsonFile, model, language);

            //api.sendMessage(t, String.valueOf(n.message().chat().id()), n.message().message_id());
            api.forwardMessageCaption(String.valueOf(n.message().chat().id()), n.message().chat().id(), n.message().message_id(), t);

        } else {
            String message = "Send me an audio and I will transcribe it \uD83D\uDC4D";
            api.sendMessage(message, String.valueOf(n.message().chat().id()));
        }
        return n.update_id();
    }
}
