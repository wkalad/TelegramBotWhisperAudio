package app;

import com.google.gson.Gson;
import dto.WhisperResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WhisperApi {

    ProcessBuilder builder;


    public WhisperApi(){
        builder = new ProcessBuilder();


    }


    public String transcribe(Path path) throws IOException, InterruptedException {

        builder.command("cmd.exe", "/c", "whisper " + path.toAbsolutePath() + " --model small --language Castilian --output_format json --output_dir .\\audios");

        Process process = builder.start();
        /*
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String text = "";
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            text = text + line;
        }
        */

        int exitCode = process.waitFor();

        if(exitCode != 0){
            throw new RuntimeException("Whisper Error, Code: " + exitCode);
        }

        Path jsonPath = Paths.get(".\\audios", "audio.json");

        String jsonContent = Files.readString(jsonPath);

        Gson gson = new Gson();

        WhisperResult whisperResult = gson.fromJson(jsonContent, WhisperResult.class);

        return whisperResult.text();


    }



}
