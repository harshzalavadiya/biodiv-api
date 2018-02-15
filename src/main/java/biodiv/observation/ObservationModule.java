package biodiv.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class ObservationModule extends ServletModule {
private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring ObservationModule Servlets");
		
		bind(Annotation.class);
		
		bind(Observation.class);
		bind(ObservationDao.class).in(Singleton.class);
		bind(ObservationService.class).in(Singleton.class);
		bind(ObservationController.class).in(Singleton.class);
		
		bind(Recommendation.class);
		bind(RecommendationVote.class);
		bind(RecommendationDao.class).in(Singleton.class);
		bind(RecommendationService.class).in(Singleton.class);
		//bind(RecommendationController.class).in(Singleton.class);
	}
}
