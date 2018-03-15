package biodiv.mail;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHtmlMailingService extends AbstractMailingService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	protected final SimpleEmail email;

	protected AbstractHtmlMailingService(MailProvider mailProvider, Configuration config, String subject, String message) throws EmailException {
		super(config, subject, message);
		this.email = mailProvider.getSimpleEmail();
		this.email.setSubject(super.subject);
		this.email.setMsg(super.message);
		this.email.buildMimeMessage();
	}

	@Override
	public void send(String toEmail) {
		log.info("Trying to send mail to: {}", toEmail);
		try {
			email.addTo(toEmail);
			email.sendMimeMessage();
			log.info("Mail sent to: {}", toEmail);
		} catch (EmailException e) {
			log.info("Failed in sending mail to: {}", toEmail);
			throw new RuntimeException(e);
		}
	}

}
