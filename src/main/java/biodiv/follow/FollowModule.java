package biodiv.follow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class FollowModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring FollowModule Servlets");
		
		bind(Follow.class);
		bind(FollowDao.class).in(Singleton.class);
		bind(FollowService.class).in(Singleton.class);
	}
}
