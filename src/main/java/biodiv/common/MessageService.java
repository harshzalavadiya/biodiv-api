package biodiv.common;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.stringtemplate.v4.ST;

public class MessageService {

	private static final String FILENAME = "i18n/messages";

	private ResourceBundle messageBundle;

	public MessageService() {
		messageBundle = ResourceBundle.getBundle(FILENAME, Locale.getDefault());
	}

	public MessageService(Locale locale) {
		messageBundle = ResourceBundle.getBundle(FILENAME, locale);
	}

	public String getMessage(String code) {
		return messageBundle.getString(code);
	}

	public String getMessage(String code, Map<String, String> params) {
		ST message = new ST(messageBundle.getString(code));
		for (Map.Entry<String, String> entry : params.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue());
			message.add(entry.getKey(), entry.getValue());
		}

		return message.render();
	}
}
