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

	private SimpleEmail simpleEmail;

	private  HtmlEmail htmlEmail;

	private String hostName;
	
	private int port;
	
	private String senderEmail;
	
	private String password;
	
	private String[] bccs;
	
	@Inject
	public MailProvider(Configuration config) throws EmailException {

		this.hostName = config.getString("mail.hostName");
		this.port = config.getInt("mail.port");
		this.senderEmail = config.getString("mail.senderEmail");
		this.password = config.getString("mail.password");
		this.bccs = config.getStringArray("mail.bcc");

//		htmlEmail = new HtmlEmail();
//		initMail(htmlEmail, hostName, port, password, senderEmail, bccs);
	}

	private void initMail(Email email, String hostName, int port, String password, String senderEmail,
			String[] bccs) throws EmailException {
		email.setHostName(hostName);
		email.setSmtpPort(port);
		email.setAuthenticator(new DefaultAuthenticator(senderEmail, password));
		email.setFrom(senderEmail);
		//email.addBcc(bccs);
	}

	public SimpleEmail getSimpleEmail() {
		simpleEmail = new SimpleEmail();
		try {
			initMail(simpleEmail, hostName, port, password, senderEmail, bccs);
		} catch (EmailException e) {
			e.printStackTrace();
		}
		return simpleEmail;
	}

	public HtmlEmail getHtmlEmail() {
		htmlEmail = new HtmlEmail();
		try {
			initMail(htmlEmail, hostName, port, password, senderEmail, bccs);
		} catch (EmailException e) {
			e.printStackTrace();
		}
		return htmlEmail;
	}
}