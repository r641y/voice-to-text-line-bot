package work.r641y.voicetotextlinebot;

import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConvertLogic {

    // スペース
    private static final String SPACE = "\\s+";

    // 拡張子
    private static final String M4A_FILE = ".m4a";
    private static final String FLAC_FILE = ".flac";


    /**
     * コマンドを実行する
     *
     * @param command
     */
    private void execute(String command) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder(toList(command));
        Process process = pb.start();
        process.waitFor();
        process.exitValue();
    }

    /**
     * ProcessBuilder用にコマンドをスペース区切りにしてリスト形式で保持する。
     *
     * @param command
     * @return
     */
    private List<String> toList(String command) {
        String[] commandSplit = command.split(SPACE);
        return Arrays.stream(commandSplit).collect(Collectors.toList());
    }


    /**
     * LINEで送信されたボイスメッセージからm4aファイル名を返却するメソッド
     *
     * @param resp
     * @param extension 拡張子
     * @return
     */
    private Optional<String> makeM4aFile(MessageContentResponse resp, String extension) {
        // tmpディレクトリに一時的に格納して、ファイルパスを返す
        try (InputStream is = resp.getStream()) {

            Path tmpFilePath = Files.createTempFile("linebot", extension);
            Files.copy(is, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            return Optional.ofNullable(tmpFilePath.toString());
        } catch (IOException e) {
            throw new RuntimeException("ファイル作成に失敗しました。");
        }
    }

    /**
     * Speech APIを使用してオーディオファイルをテキストに変換する処理
     */
    public List<String> convertVoiceToText(MessageEvent<AudioMessageContent> audioEvent) throws Exception {

        // LINE Botを利用するためのトークンの取得
        final LineMessagingClient client = LineMessagingClient
                .builder(System.getenv("LINE_BOT_CHANNEL_TOKEN"))
                .build();
        final String messageId = audioEvent.getMessage().getId();
        final MessageContentResponse messageContentResponse = client.getMessageContent(messageId).get();

        // file name before conversion
        Optional<String> beforeConversionFile = makeM4aFile(messageContentResponse, M4A_FILE);

        // file name after conversion
        Optional<String> afterConversionFile = Optional.of(beforeConversionFile.get().replace(M4A_FILE, FLAC_FILE));

        // command
        String command = "ffmpeg -y -i " + beforeConversionFile.get() + " -sample_fmt s16 " + afterConversionFile.get();

        // execute command
        execute(command);

        SpeechToText speechToText = new SpeechToText();
        List<SpeechRecognitionResult> results = speechToText.VoiceToText(afterConversionFile);

        return results.stream()
                .map(e -> e.getAlternativesList().get(0).getTranscript())
                .collect(Collectors.toList());
    }
}