package app;

import com.google.gson.Gson;
import dto.WhisperResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WhisperApi {

    private final ProcessBuilder builder;
    private final Gson gson;

    public WhisperApi() {
        builder = new ProcessBuilder();
        gson = new Gson();

    }

    public String transcribe(Path audioFile, Path jsonFile) throws IOException, InterruptedException {

        builder.command("cmd.exe", "/c", "whisper " + audioFile.toAbsolutePath() + " --model small --output_format json --output_dir .\\audios");

        Process process = builder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Whisper Error, Code: " + exitCode);
        }

        String jsonContent = Files.readString(jsonFile);

        WhisperResult whisperResult = gson.fromJson(jsonContent, WhisperResult.class);

        return whisperResult.text();


    }


}
