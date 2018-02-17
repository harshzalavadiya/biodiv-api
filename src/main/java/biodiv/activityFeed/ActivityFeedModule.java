package biodiv.activityFeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class ActivityFeedModule extends ServletModule {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring ActivityFeedModule Servlets");
		bind(ActivityFeed.class);
		bind(ActivityFeedDao.class).in(Singleton.class);
		bind(ActivityFeedService.class).in(Singleton.class);
		bind(ActivityFeedController.class).in(Singleton.class);

	}
}
