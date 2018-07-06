package biodiv.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.customField.CustomField;
import biodiv.customField.CustomFieldController;
import biodiv.customField.CustomFieldDao;
import biodiv.customField.CustomFieldService;
import biodiv.customField.CustomFieldsGroup18;

public class RecommendationVoteModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring RecommendationVoteModule Servlets");
	
		bind(RecommendationVote.class);
		bind(RecommendationVoteDao.class);
		
		bind(RecommendationVoteService.class).in(Singleton.class);
		
		//bind(RecommendationVoteController.class).in(Singleton.class);
	}
}
