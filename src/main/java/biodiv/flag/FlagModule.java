package biodiv.flag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.rating.RatingLink;
import biodiv.rating.RatingLinkDao;
import biodiv.rating.RatingLinkService;

public class FlagModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring FlagModule Servlets");
		bind(Flag.class);
		bind(FlagDao.class).in(Singleton.class);
		bind(FlagService.class).in(Singleton.class);
		bind(FlagController.class).in(Singleton.class);
		
	}
}
