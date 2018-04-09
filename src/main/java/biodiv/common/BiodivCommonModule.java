package biodiv.common;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.auth.CustomOAuth20Authenticator;
import biodiv.auth.CustomOAuth2ProfileCreator;
import biodiv.auth.FacebookClient;
import biodiv.common.eml.Contact;
import freemarker.template.TemplateExceptionHandler;

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
	
	@Provides @Singleton
	protected freemarker.template.Configuration getfreemarkerConfig() throws IOException, URISyntaxException {
		//log.debug("Creating freemarker");
		System.out.println("testing path^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^6 "+this.getClass().getClassLoader().getResource("mailingTemplates").toURI());
		freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_27);
		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		
		System.out.println("testing version object"+cfg.equals(null));
		
		cfg.setDirectoryForTemplateLoading(new File(this.getClass().getClassLoader().getResource("mailingTemplates").toURI()));
		
		
		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		cfg.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
		cfg.setWrapUncheckedExceptions(true);
		
		return cfg;
	}
}
