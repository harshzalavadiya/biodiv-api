package biodiv.rating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;


public class RatingModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring UserGroupModule Servlets");
		bind(Rating.class);
		bind(RatingDao.class).in(Singleton.class);
		bind(RatingService.class).in(Singleton.class);
		//bind(RatingController.class).in(Singleton.class);
		
	}
}
