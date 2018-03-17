package biodiv.mail;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.EmailException;

import com.google.inject.Inject;

public class DownloadMailingService extends AbstractSimpleMailingService {
	
	@Inject
	public DownloadMailingService(Configuration config, MailProvider mailProvider) throws EmailException {
		super(mailProvider, config, "mail.download.subject", "mail.download.message");
	}
}
