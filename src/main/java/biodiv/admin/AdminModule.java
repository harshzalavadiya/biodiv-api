package biodiv.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class AdminModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configureServlets() {
		log.debug("Configuring AdminModule Servlets");

		bind(AdminController.class).in(Singleton.class);;
}
	
}
