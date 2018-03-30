package biodiv.userGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class UserGroupModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring UserGroupModule Servlets");
		bind(UserGroup.class);
		bind(UserGroupDao.class).in(Singleton.class);
		bind(UserGroupService.class).in(Singleton.class);
		bind(UserGroupController.class).in(Singleton.class);
		
		bind(Newsletter.class);
		bind(NewsletterDao.class).in(Singleton.class);
		bind(NewsletterService.class).in(Singleton.class);
		
		bind(UserGroupMailingService.class).in(Singleton.class);
	}
}
