package biodiv.species;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;


public class AcceptedSynonymModule extends ServletModule {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring AcceptedSynonymModule Servlets");
		
		
		bind(AcceptedSynonym.class);
		bind(AcceptedSynonymDao.class).in(Singleton.class);
		bind(AcceptedSynonymService.class).in(Singleton.class);
		//bind(AcceptedSynoymController.class).in(Singleton.class);
		
		
		
	}
}
