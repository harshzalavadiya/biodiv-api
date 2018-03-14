package biodiv.mail;

import org.apache.commons.configuration2.Configuration;

import com.google.inject.Inject;

public class DownloadMailingService extends AbstractSimpleMailingService {
	
	@Inject
	public DownloadMailingService(Configuration config, MailProvider mailProvider) {
		super(mailProvider, config, "mail.download.subject", "mail.download.message");
	}
}
