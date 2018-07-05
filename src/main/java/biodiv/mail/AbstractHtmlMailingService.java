package biodiv.mail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.MailingTemplate;
import biodiv.user.User;
import biodiv.user.UserService;

public abstract class AbstractHtmlMailingService extends AbstractMailingService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	// protected final HtmlEmail email;

	@Inject
	MailProvider mailProvider;

	@Inject
	Configuration config;

	@Inject
	UserService userService;

	private Deque<HtmlEmail> queueOfAllMails = new ArrayDeque<HtmlEmail>();

	private Boolean isThreadRunning = false;

	protected AbstractHtmlMailingService(MailProvider mailProvider, Configuration config, String subject,
			String message) throws EmailException {
		super(config, subject, message);
		// this.email = mailProvider.getHtmlEmail();
		// this.email.setSubject(super.subject);
		// //this.email.setMsg(super.message);
		// this.email.setContent(aObject, aContentType);
		// this.email.buildMimeMessage();
	}

	public HtmlEmail buildMessage(String subject, String message, String toEmail, freemarker.template.Template temp,
			Map<String, Object> root) throws Exception {

		try {
			Writer out = new StringWriter();
			temp.process(root, out);
			HtmlEmail email;
			email = this.mailProvider.getHtmlEmail();
			email.setSubject(subject);
			// email.setMsg(message);
			email.addTo(toEmail);
			// if(count == 1){
			// email.addBcc(config.getStringArray("mail.bcc"));
			// }

			// email.addBcc(this.config.getString("mail.bcc"));
			// email.setFrom(this.config.getString("mail.senderEmail"));

			email.setContent(out.toString(), "text/html; charset=utf-8");
			email.buildMimeMessage();

			addMailToQueue(email);
			return email;
		} catch (Exception e) {
			throw e;
		}

	}

	private void addMailToQueue(HtmlEmail email) {

		queueOfAllMails.addLast(email);
	}

	public Boolean isAnyThreadActive() {
		return this.isThreadRunning;
	}

	public void send(HtmlEmail email) {
		// log.info("Trying to send mail to: {}", email.;
		try {
			// email.addTo(toEmail);
			email.sendMimeMessage();

			System.out.println("mimemessage " + email.getMimeMessage());
			log.info("Mail sent to: {}", email.getToAddresses());
		} catch (EmailException e) {
			log.info("Failed in sending mail to: {}");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		if (isAnyThreadActive()) {
			return;
		}
		this.isThreadRunning = true;

		while (!queueOfAllMails.isEmpty()) {
			System.out.println("size of deque " + queueOfAllMails.size());
			send(queueOfAllMails.removeFirst());
			System.out.println("size of deque " + queueOfAllMails.size());
		}

		this.isThreadRunning = false;
	}

	public boolean isTheFollowerInBccList(String email) {
		String[] bccPeople = config.getStringArray("mail.bcc");
		for (String bcc : bccPeople) {
			if (bcc.equals(email)) {
				return true;
			}
		}
		return false;
	}

	public List<User> getAllBccPeople() {
		List<User> bccs = new ArrayList<User>();
		String[] bccPeople = config.getStringArray("mail.bcc");
		for (String bcc : bccPeople) {
			// User u = userService.findByEmail(bcc);
			User u = new User();
			u.setName("admin");
			u.setEmail(bcc);
			u.setId(1L);
			if (u != null) {
				bccs.add(u);
			}

		}
		return bccs;
	}

}
