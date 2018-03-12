package biodiv.speciesPermission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;



public class SpeciesPermissionModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring UserGroupModule Servlets");
		bind(SpeciesPermission.class);
		bind(SpeciesPermissionDao.class).in(Singleton.class);
		bind(SpeciesPermissionService.class).in(Singleton.class);
		bind(SpeciesPermissionController.class).in(Singleton.class);
		
	}
}
