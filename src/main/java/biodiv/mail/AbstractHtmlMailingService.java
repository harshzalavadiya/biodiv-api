package biodiv.mail;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHtmlMailingService extends AbstractMailingService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	protected final MailProvider mailProvider;

	protected AbstractHtmlMailingService(MailProvider mailProvider, Configuration config, String subject, String message) {
		super(config, subject, message);
		this.mailProvider = mailProvider;
	}

	@Override
	public void send(String toEmail) {
		log.info("Trying to send mail to: {}", toEmail);
		try {
			HtmlEmail email = mailProvider.getHtmlEmail();
			email.setSubject(subject);
			email.setHtmlMsg(message);
			email.addTo(toEmail);
			email.send();
			log.info("Mail sent to: {}", toEmail);
		} catch (EmailException e) {
			log.info("Failed in sending mail to: {}", toEmail);
			throw new RuntimeException(e);
		}
	}

}
