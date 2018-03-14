package biodiv.mail;

import org.apache.commons.configuration2.Configuration;

public abstract class AbstractMailingService {

	protected final String subject;

	protected final String message;

	protected AbstractMailingService(Configuration config, String subject, String message) {
		this.subject = config.getString(subject);
		this.message = config.getString(message);
	}

	public abstract void send(String toEmail);	
}
