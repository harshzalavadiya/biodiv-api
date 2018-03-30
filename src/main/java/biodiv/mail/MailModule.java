package biodiv.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.userGroup.Newsletter;
import biodiv.userGroup.NewsletterDao;
import biodiv.userGroup.NewsletterService;
import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupController;
import biodiv.userGroup.UserGroupDao;
import biodiv.userGroup.UserGroupService;

public class MailModule extends ServletModule {

	@Override
	protected void configureServlets(){
		bind(MailProvider.class).in(Singleton.class);
		bind(DownloadMailingService.class).in(Singleton.class);
	}
	
}