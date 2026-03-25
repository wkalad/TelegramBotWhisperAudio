package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class WhisperApi {

    ProcessBuilder builder;


    public WhisperApi(){
        builder = new ProcessBuilder();


    }


    public String transcribe(Path path) throws IOException, InterruptedException {

        builder.command("cmd.exe", "/c", "whisper " + path.toAbsolutePath() + " --model small --language Castilian --output_format txt --output_dir .\\audios");

        Process process = builder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String text = "";
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            text = text + line;
        }

        int exitCode = process.waitFor();

        return text;


    }



}
