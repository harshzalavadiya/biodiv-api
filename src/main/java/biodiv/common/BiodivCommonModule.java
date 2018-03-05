package biodiv.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.common.eml.Contact;

public class BiodivCommonModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring BiodivCommonModule Servlets");
		
		bind(Habitat.class);
		bind(Ufile.class);
		bind(Contact.class);
		
		bind(SpeciesGroup.class);
		bind(SpeciesGroupMapping.class);
		bind(SpeciesGroupDao.class).in(Singleton.class);
		bind(SpeciesGroupService.class).in(Singleton.class);
		bind(SpeciesGroupController.class).in(Singleton.class);
		
		bind(Language.class);
		bind(LanguageDao.class).in(Singleton.class);
		bind(LanguageService.class).in(Singleton.class);
		//bind(LanguageController.class).in(Singleton.class);

		bind(License.class);
		bind(LicenseDao.class).in(Singleton.class);
		bind(LicenseService.class).in(Singleton.class);
		bind(LicenseController.class).in(Singleton.class);
		
		bind(MessageService.class).in(Singleton.class);
		bind(MailService.class).in(Singleton.class);

		bind(NakshaUrlService.class).in(Singleton.class);
	}
}
