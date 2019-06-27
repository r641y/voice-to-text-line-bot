package work.r641y.voicetotextlinebot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.linecorp.bot.model.message.TextMessage;


public class Lambda implements RequestHandler<Object, Object> {

	@Override
	public Object handleRequest(Object input, Context context) {

		return new TextMessage("テキスト送信ありがとうございます！");
	}


}
