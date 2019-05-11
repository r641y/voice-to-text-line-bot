package work.r641y.voicetotextlinebot;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Google Cloud Platform -- Speech to text API --
 */
public class SpeechToText {

    public List<SpeechRecognitionResult> VoiceToText(Optional<String> filename) throws IOException {
        
        // Instantiates a client
        SpeechClient speechClient = SpeechClient.create();
        Path newFilePath = Paths.get(filename.get());

        // Reads the audio file into memory
        // Path path = Paths.get(fileName).normalize().toAbsolutePath();
        byte[] data = Files.readAllBytes(newFilePath);
        ByteString audioBytes = ByteString.copyFrom(data);

        // Builds the sync recognize request
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
                .setSampleRateHertz(16000)
                .setLanguageCode("ja-JP")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        // Performs speech recognition on the audio file
        RecognizeResponse response = speechClient.recognize(config, audio);
        List<SpeechRecognitionResult> resultList = response.getResultsList();

        return resultList;
    }
}
