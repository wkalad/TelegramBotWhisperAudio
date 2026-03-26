package telegram;

import com.google.gson.Gson;
import dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;

public class TelegramBotApi {

    private final String API_BASE = "https://api.telegram.org/bot";
    private final String API_BASE_FILE = "https://api.telegram.org/file/bot";
    private final String token;
    private final Gson gson;
    private final HttpClient client;

    public TelegramBotApi(String token) {
        client = HttpClient.newHttpClient();
        this.token = token;
        gson = new Gson();
    }

    public List<Update> getUpdates(int offset) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE+token+"/getUpdates?offset="+offset))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("getUpdates HTTP " + response.statusCode());
        }

        Result result = gson.fromJson(response.body(), Result.class);

        if (!result.ok()) {
            throw new RuntimeException("getUpdates ErrorApi " + result);
        }

        return result.result();
    }

    public void sendMessage(String message, String chatId) throws IOException, InterruptedException {

        SendMessagePost sendMessagePost = new SendMessagePost(chatId, message);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + token + "/sendMessage"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sendMessagePost)))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("sendMessage HTTP " + response.statusCode());
        }

        SendMessageResponse sendMessageResponse = gson.fromJson(response.body(), SendMessageResponse.class);

        if (!sendMessageResponse.ok()) {
            throw new RuntimeException("sendMessage ErrorApi " + sendMessageResponse);
        }

    }

    public void sendMessage(String message, String chatId, long messageId) throws IOException, InterruptedException {

        SendMessagePostReply sendMessagePostReply = new SendMessagePostReply(chatId, message, messageId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + token + "/sendMessage"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(sendMessagePostReply)))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("sendMessage HTTP " + response.statusCode());
        }

        SendMessageResponse sendMessageResponse = gson.fromJson(response.body(), SendMessageResponse.class);

        if (!sendMessageResponse.ok()) {
            throw new RuntimeException("sendMessage ErrorApi " + sendMessageResponse);
        }

    }

    public void downloadAudio(String fileId, Path audioPath) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + token + "/getFile?file_id=" + fileId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error fetching audio. HTTP " + response.statusCode());
        }

        GetFile file = gson.fromJson(response.body(), GetFile.class);

        request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_FILE + token + "/" + file.result().file_path()))
                .GET()
                .build();

        HttpResponse<Path> responseAudio = client.send(request, HttpResponse.BodyHandlers.ofFile(audioPath));

        if (responseAudio.statusCode() != 200) {
            throw new RuntimeException("Error downloading audio. HTTP " + responseAudio.statusCode());
        }

    }
}
