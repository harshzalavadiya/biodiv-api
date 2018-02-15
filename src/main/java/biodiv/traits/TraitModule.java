package biodiv.traits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class TraitModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring TraitModule Servlets");
		
		bind(Trait.class);
		bind(TraitValue.class);
		bind(TraitDao.class).in(Singleton.class);
		bind(TraitService.class).in(Singleton.class);
		bind(TraitTaxonomyDefinition.class).in(Singleton.class);
		
		bind(Fact.class);
	}
}
