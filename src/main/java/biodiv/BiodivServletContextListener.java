package biodiv;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import biodiv.activityFeed.ActivityFeedModule;
import biodiv.auth.AuthModule;
import biodiv.comment.CommentModule;
import biodiv.common.BiodivCommonModule;
import biodiv.customField.CustomFieldModule;
import biodiv.dataset.DatasetModule;
import biodiv.follow.FollowModule;
import biodiv.maps.MapModule;
import biodiv.observation.ObservationModule;
import biodiv.taxon.TaxonModule;
import biodiv.traits.TraitModule;
import biodiv.user.UserModule;
import biodiv.userGroup.UserGroupModule;

public class BiodivServletContextListener extends GuiceServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
				new ServletModule() {

					@Override
					protected void configureServlets() {
						log.debug("Configuring Servlets");

						Configurations configs = new Configurations();
						try
						{
							String ENV_NAME = "BIODIV_API_CONFIG";
							log.info("Reading configuration from : {}", System.getenv(ENV_NAME));
						    Configuration config = configs.properties(new File(System.getenv(ENV_NAME)));
						    bind(Configuration.class).toInstance(config);
						}
						catch (ConfigurationException cex)
						{
							cex.printStackTrace();
						}
						
						// rest("/*").packages("biodiv");

						// INTERCEPTOR
						ResourceInterceptor interceptor = new ResourceInterceptor();
						bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), interceptor);
						bind(ResourceInterceptor.class).toInstance(interceptor);
						requestInjection(interceptor);

						// FILTERS
						//bind(BiodivResponseFilter.class).in(Singleton.class);

					}
				},
				new BiodivCommonModule(), 
				new ActivityFeedModule(), 
				new AuthModule(),
				new CommentModule(), 
				new CustomFieldModule(), 
				new DatasetModule(), 
				new FollowModule(), 				
				new MapModule(),
				new ObservationModule(), 
				new TaxonModule(), 
				new TraitModule(), 
				new UserModule(), 
				new UserGroupModule()
				);
	}

}
