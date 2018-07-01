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
	}

	public HtmlEmail buildActivationMailMessage(String toEmail, Map<String, Object> params) throws Exception {

		try {
			String sub = "Activate your account with "+params.get("domain");
			freemarker.template.Template temp = cfg.getTemplate("activationMail.ftlh");
			HtmlEmail email = buildMessage(sub, this.message, toEmail, temp, params);
			
			return email;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public HtmlEmail buildWelcomeMailMessage(String toEmail, Map<String, Object> params) throws Exception {

		try {
			String sub = "Welcome to "+params.get("domain");
			freemarker.template.Template temp = cfg.getTemplate("welcomeMail.ftlh");
			HtmlEmail email = buildMessage(sub, this.message, toEmail, temp, params);
			return email;
		} catch (Exception e) {
			throw e;
		}
	}

	public HtmlEmail buildResetPasswordMailMessage(String toEmail, Map<String, Object> params) throws Exception {
		try {
			String sub = "Reset password with "+params.get("domain");
			freemarker.template.Template temp = cfg.getTemplate("resetPasswordMail.ftlh");
			HtmlEmail email = buildMessage(sub, this.message, toEmail, temp, params);
			return email;
		} catch (Exception e) {
			throw e;
		}
	}
}
