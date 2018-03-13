package biodiv.mail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class MailingService {

	@Inject
	MailProvider mailProvider;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public void sendSimpleEmail(String toEmail, String subject, String message) {
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
