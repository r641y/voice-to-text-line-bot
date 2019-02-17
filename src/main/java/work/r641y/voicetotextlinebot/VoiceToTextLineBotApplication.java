package work.r641y.voicetotextlinebot;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@LineMessageHandler
public class VoiceToTextLineBotApplication {


    public static void main(String[] args) {
        SpringApplication.run(VoiceToTextLineBotApplication.class, args);
    }

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> textEevent) {
        return new TextMessage("テキスト送信ありがとうございます！");
    }

    @EventMapping
    public Message handleImageMessageEvent(MessageEvent<ImageMessageContent> imageEvent) {
        return new TextMessage("動画送信ありがとうございます！");
    }

    @EventMapping
    public List<Message> handleAudioMessage(MessageEvent<AudioMessageContent> audioEvent) throws Exception {
        ConvertLogic convertLogic = new ConvertLogic();
        List<String> message = convertLogic.convertVoiceToText(audioEvent);
        List<Message> messages = new ArrayList<>();
        for(String mess:  message){
            messages.add(new TextMessage(mess));
        }
        return messages;
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}