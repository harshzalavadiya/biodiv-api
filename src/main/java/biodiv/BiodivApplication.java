package biodiv;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import biodiv.common.JTSObjectMapperProvider;

@ApplicationPath("/")
public class BiodivApplication extends ResourceConfig {// javax.ws.rs.core.Application
														// {

	private final Logger log = LoggerFactory.getLogger(BiodivApplication.class);

	public BiodivApplication() {

		System.out.println("Starting Biodiv Api Application");

		// auto scanning of all classed for resources providers and features
		packages("biodiv");
		register(RolesAllowedDynamicFeature.class);
		register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
		register(org.glassfish.jersey.filter.LoggingFilter.class);
		// register(org.glassfish.jersey.server.validation.ValidationFeature.class);
		// register(org.glassfish.jersey.server.spring.SpringComponentProvider.class);
		
		register(JTSObjectMapperProvider.class);

		register(org.glassfish.jersey.jackson.JacksonFeature.class);

		// optimization to disable scanning all packages for providers and
		// features
		// property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
	}

	/*
	 * @Override public Set<Class<?>> getClasses() { Set<Class<?>> resources =
	 * new java.util.HashSet<>();
	 * 
	 * final Reflections reflection = new Reflections("biodiv"); resources =
	 * reflection.getTypesAnnotatedWith(Path.class);
	 * 
	 * //this will register Jackson JSON providers
	 * resources.add(org.glassfish.jersey.jackson.JacksonFeature.class); return
	 * resources; }
	 */
}
