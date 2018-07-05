package biodiv.rating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class RatingLinkModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring UserGroupModule Servlets");
		bind(RatingLink.class);
		bind(RatingLinkDao.class).in(Singleton.class);
		bind(RatingLinkService.class).in(Singleton.class);
		//bind(RatingLinkController.class).in(Singleton.class);
		
	}
}
