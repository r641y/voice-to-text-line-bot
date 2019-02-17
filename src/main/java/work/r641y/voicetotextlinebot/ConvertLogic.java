package work.r641y.voicetotextlinebot;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConvertLogic {


    private Optional<String> makeM4aFile(MessageContentResponse resp, String extension) {
        // tmpディレクトリに一時的に格納して、ファイルパスを返す
        try (InputStream is = resp.getStream()) {

            Path tmpFilePath = Files.createTempFile("linebot", extension);
            Files.copy(is, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            return Optional.ofNullable(tmpFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    /**
     * Demonstrates using the Speech API to transcribe an audio file.
     */
    public List<String> convertVoiceToText(MessageEvent<AudioMessageContent> audioEvent) throws Exception {

        String messageId = audioEvent.getMessage().getId();
        System.out.println(audioEvent.hashCode());
        System.out.println(audioEvent.getMessage().getContentProvider());
        List<String> ans = new ArrayList<>();

        // 以下の処理で取得している
        final LineMessagingClient client = LineMessagingClient
                .builder(System.getenv("LINE_BOT_CHANNEL_TOKEN"))
                .build();

        final MessageContentResponse messageContentResponse;
        Optional<String> opt = Optional.empty();

        try {
            messageContentResponse = client.getMessageContent(messageId).get();
            opt = makeM4aFile(messageContentResponse, ".m4a");
            System.out.println("file-path: " + opt.orElseGet(() -> "ファイル書き込みNG"));
        } catch (Exception e) {

        }

        // m4aからflacに変換
        // ここは外部コマンドを叩く

        String aa = opt.get();
        Optional<String> opt2 = Optional.of(aa.replace(".m4a", ".flac"));

        System.out.println(opt.get());

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(opt.get());
        command.add("-sample_fmt");
        command.add("s16");
        command.add(opt2.get());

        try {
            ProcessBuilder pb = new ProcessBuilder(command);

            Process process = pb.start();
            process.waitFor();
            int ret = process.exitValue();
            System.out.println("結果：" + ret);


        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }


        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create()) {

            // The path to the audio file to transcribe
//            String fileName = "src/main/resources/voice/sample1.flac";

            Path path = Paths.get(opt2.get());


            // Reads the audio file into memory
//            Path path = Paths.get(fileName).normalize().toAbsolutePath();
            System.out.println("aaa" + path.toString());
            byte[] data = Files.readAllBytes(path);
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
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                ans.add(alternative.getTranscript());
            }
        }
        return ans;
    }
}