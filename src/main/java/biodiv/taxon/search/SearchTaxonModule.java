package biodiv.taxon.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class SearchTaxonModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configureServlets() {
		log.debug("Configuring SearchTaxon Servlets");

		bind(SearchTaxon.class).in(Singleton.class);
	}
}
