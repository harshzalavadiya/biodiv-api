package biodiv.allsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class AllSearchModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configureServlets() {
		log.debug("Configuring AllSearchModule Servlets");

		bind(AllSearchService.class).in(Singleton.class);
		bind(AllSearchController.class).in(Singleton.class);
	}
}
