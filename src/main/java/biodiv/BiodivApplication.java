package biodiv;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import biodiv.common.JTSObjectMapperProvider;
import biodiv.observation.ObservationService;
import biodiv.userGroup.UserGroupService;

@ApplicationPath("/")
public class BiodivApplication extends ResourceConfig {// javax.ws.rs.core.Application
														// {

	//private final Logger log = LoggerFactory.getLogger(BiodivApplication.class);
	private Map<String,Object> getConfig= new HashMap<String,Object>();
	
	
	public BiodivApplication() {

		System.out.println("Starting Biodiv Api Application");
//		 Yaml yaml = new Yaml(); 
//		 try { 
//	    	 Map<String, Object> list = (HashMap<String, Object>) yaml.load(new FileReader("/home/abhinav/git/biodiv-api/conf/Config.yml")); 
//	    	 for (Map.Entry<String, Object> entry : list.entrySet()){
//	    	     System.out.println(entry.getKey() + "/" + entry.getValue());
//	    	 }	    	 
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    	System.out.println(e);
//	    }
//		 
		// auto scanning of all classed for resources providers and features
		packages("biodiv");
		register(new AbstractBinder(){
		@Override
		protected void configure() {
		    bind(MyInterceptionService.class)
            .to(org.glassfish.hk2.api.InterceptionService.class)
            .in(Singleton.class);
//		    bind(ObservationService.class)
//            .to(ObservationService.class)
//            .in(Singleton.class);
//		    bind(UserGroupService.class)
//            .to(UserGroupService.class)
//            .in(Singleton.class);
		    bind(ObservationService.class)
            .to(ObservationService.class)
            .in(Singleton.class);
		    bind(UserGroupService.class)
            .to(UserGroupService.class)
            .in(Singleton.class);
		}
	});
		
//		register(new AbstractBinder(){
//			@Override
//			protected void configure() {
//			    bindFactory(SessionFactoryFactory.class)
//			            .to(SessionFactory.class)
//			            .in(Singleton.class);
//			    bindFactory(SFFactory.class)
//			            .to(Session.class)
//			            .in(RequestScoped.class);
//			}
//		});
		register(RolesAllowedDynamicFeature.class);
		register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
		register(new LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY, Integer.MAX_VALUE));
		//register(org.glassfish.jersey.logging.LoggingFeature.class);
        //property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_TEXT);
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
	public Map<String, Object> getGetConfig() {
		return getConfig;
	}
	public void setGetConfig(Map<String, Object> getConfig) {
		this.getConfig = getConfig;
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
