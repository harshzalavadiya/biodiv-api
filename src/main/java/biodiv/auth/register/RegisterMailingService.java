package biodiv.auth.register;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.MessageService;
import biodiv.mail.AbstractHtmlMailingService;
import biodiv.mail.MailProvider;

public class RegisterMailingService extends AbstractHtmlMailingService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private MessageService messageService;
	
	@Inject
	freemarker.template.Configuration cfg;

	// final String subject = "Download request";
	final String message = "";

	@Inject
	public RegisterMailingService(Configuration config, MailProvider mailProvider) throws EmailException {
		super(mailProvider, config, "mail.register.subject", "mail.register.message");
		System.out.println("****************************************************8 " + config);
		System.out.println("****************************************************8 " + mailProvider);
	}

	public HtmlEmail buildActivationMailMessage(String toEmail, Map<String, Object> params) throws Exception {

		try {
			String sub = "Activate your account with "+params.get("domain");
			log.debug(sub);
//			String body = messageService.getMessage("register.emailBody", params);
//			log.debug(body);

			freemarker.template.Template temp = cfg.getTemplate("activationMail.ftlh");
			
			//RegistrationActivationMailDataModel dm = new RegistrationActivationMailDataModel(params);
			
			//Map<String, Object> root = dm.getRoot();
			
			HtmlEmail email = buildMessage(sub, this.message, toEmail, temp, params);
			
			return email;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public HtmlEmail buildWelcomeMailMessage(String toEmail, Map<String, Object> params) throws Exception {

		try {
			String sub = "Welcome to "+params.get("domain");
			log.debug(sub);
//			String body = messageService.getMessage("register.emailBody", params);
//			log.debug(body);

			freemarker.template.Template temp = cfg.getTemplate("welcomeMail.ftlh");
			
			//RegistrationActivationMailDataModel dm = new RegistrationActivationMailDataModel(params);
			
			//Map<String, Object> root = dm.getRoot();
			
			HtmlEmail email = buildMessage(sub, this.message, toEmail, temp, params);
			
			return email;
		} catch (Exception e) {
			throw e;
		}
	}
}
