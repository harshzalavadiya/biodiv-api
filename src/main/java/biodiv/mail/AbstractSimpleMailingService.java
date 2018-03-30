package biodiv.mail;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimpleMailingService extends AbstractMailingService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	//protected final SimpleEmail email;
	@Inject
	MailProvider mailProvider;
	
	@Inject
	Configuration config;
	
	protected AbstractSimpleMailingService(MailProvider mailProvider, Configuration config, String subject, String message) throws EmailException {
		super(config, subject, message);
//		System.out.println("****************************************************8 "+mailProvider);
//		System.out.println("****************************************************8 "+config);
//		System.out.println("****************************************************8 "+subject);
//		System.out.println("****************************************************8 "+message);
//		this.email = mailProvider.getSimpleEmail();
//		this.email.setSubject(super.subject);
//		this.email.setMsg(super.message);
//		this.email.buildMimeMessage();
	}

	public SimpleEmail buildMessage(String subject,String message,String toEmail) throws EmailException{
		
		try{
			SimpleEmail email;
			email = this.mailProvider.getSimpleEmail();
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(toEmail);
//			email.addBcc(this.config.getString("mail.bcc"));
//			email.setFrom(this.config.getString("mail.senderEmail"));
			email.buildMimeMessage();
			return email;
		}catch(Exception e){
			throw e;
		}
		
	}
	
	public void send(SimpleEmail email) {
		//log.info("Trying to send mail to: {}", email.;
		try {
			//email.addTo(toEmail);
			email.sendMimeMessage();
			System.out.println("mimemessage "+email.getMimeMessage());
			log.info("Mail sent to: {}");
		} catch (EmailException e) {
			log.info("Failed in sending mail to: {}");
			throw new RuntimeException(e);
		}
	}
}
