package biodiv.mail;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MailProvider {

	private final SimpleEmail simpleEmail;

	private final HtmlEmail htmlEmail;

	@Inject
	public MailProvider(Configuration config) throws EmailException {

		String hostName = config.getString("mail.hostName");
		int port = config.getInt("mail.port");
		String senderEmail = config.getString("mail.senderEmail");
		String password = config.getString("mail.password");
		String[] bccs = config.getStringArray("mail.bcc");

		simpleEmail = new SimpleEmail();
		initMail(simpleEmail, hostName, port, password, senderEmail, bccs);

		htmlEmail = new HtmlEmail();
		initMail(htmlEmail, hostName, port, password, senderEmail, bccs);
	}

	private void initMail(Email email, String hostName, int port, String password, String senderEmail,
			String[] bccs) throws EmailException {
		email.setHostName(hostName);
		email.setSmtpPort(port);
		email.setAuthenticator(new DefaultAuthenticator(senderEmail, password));
		email.setFrom(senderEmail);
		email.addBcc(bccs);
	}

	public SimpleEmail getSimpleEmail() {
		return simpleEmail;
	}

	public HtmlEmail getHtmlEmail() {
		return htmlEmail;
	}
}