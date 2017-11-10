package biodiv;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

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
		register(org.glassfish.jersey.logging.LoggingFeature.class);
        property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_TEXT);
		// register(org.glassfish.jersey.server.validation.ValidationFeature.class);
		// register(org.glassfish.jersey.server.spring.SpringComponentProvider.class);

        //Object mapper for geometry 
		register(JTSObjectMapperProvider.class);

		// create custom ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        hibernate5Module.enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        objectMapper.registerModule(hibernate5Module);
        
        //objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true); // Different from default so you can test it :)
        //objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        
        // create JsonProvider to provide custom ObjectMapper
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);

        register(provider);
        
      
        
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
