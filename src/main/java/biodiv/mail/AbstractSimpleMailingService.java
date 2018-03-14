package biodiv.mail;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimpleMailingService extends AbstractMailingService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	protected final MailProvider mailProvider;

	protected AbstractSimpleMailingService(MailProvider mailProvider, Configuration config, String subject, String message) {
		super(config, subject, message);
		this.mailProvider = mailProvider;
	}

	@Override
	public void send(String toEmail) {
		log.info("Trying to send mail to: {}", toEmail);
		try {
			SimpleEmail email = mailProvider.getSimpleEmail();
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(toEmail);
			email.send();
			log.info("Mail sent to: {}", toEmail);
		} catch (EmailException e) {
			log.info("Failed in sending mail to: {}", toEmail);
			throw new RuntimeException(e);
		}
	}
}
