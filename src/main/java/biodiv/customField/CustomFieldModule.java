package biodiv.customField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class CustomFieldModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring CustomFieldModule Servlets");
	
		bind(CustomField.class);
		bind(CustomFieldsGroup18.class);
		
		bind(CustomFieldDao.class).in(Singleton.class);
		bind(CustomFieldService.class).in(Singleton.class);
		
		bind(CustomFieldController.class).in(Singleton.class);
	}
}
