package biodiv.Checklists;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class ChecklistsModule extends ServletModule {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring ObservationModule Servlets");
		
		
		bind(Checklists.class);
		bind(ChecklistsDao.class).in(Singleton.class);
		bind(ChecklistsService.class).in(Singleton.class);
		//bind(ChecklistsController.class).in(Singleton.class);
		
		
		
	}
}
