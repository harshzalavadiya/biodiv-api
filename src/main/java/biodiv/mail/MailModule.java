package biodiv.mail;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class MailModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MailProvider.class).in(Scopes.SINGLETON);
		bind(DownloadMailingService.class).in(Scopes.SINGLETON);
	}
}