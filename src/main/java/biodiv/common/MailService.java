package biodiv.common;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailService {

	final String senderEmailId = "notification@indiabiodiversity.org";
	final String senderPassword = "****";
	final String emailSMTPserver = "127.0.0.1";
	final String emailSMTPserverPort = "25";
	final String DEFAULT_CONTENT_TYPE = "text/html; charset=utf-8";

	public void sendMail(String to, String subject, String msg) {
		sendMail(senderEmailId, to, subject, msg, DEFAULT_CONTENT_TYPE);
	}

	public void sendMail(String from, String to, String subject, String msg, String contentType) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", emailSMTPserver);
		props.put("mail.smtp.port", emailSMTPserverPort);

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			// message.setContent(msg, contentType);
			Multipart multipart = new MimeMultipart("alternative");

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText("", "utf-8");

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(msg, contentType);

			multipart.addBodyPart(textPart);
			multipart.addBodyPart(htmlPart);
			message.setContent(multipart);

			Transport.send(message);
			System.out.println("Email send successfully.");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(senderEmailId, senderPassword);
		}
	}

}
