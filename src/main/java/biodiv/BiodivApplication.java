package biodiv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.Injector;

import biodiv.common.JTSObjectMapperProvider;

@ApplicationPath("/")
public class BiodivApplication extends ResourceConfig {// javax.ws.rs.core.Application
														// {
	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	private Map<String, Object> getConfig = new HashMap<String, Object>();

	public BiodivApplication() {

		log.debug("Starting Biodiv Api Application");
		// Yaml yaml = new Yaml();
		// try {
		// Map<String, Object> list = (HashMap<String, Object>) yaml.load(new
		// FileReader("/home/abhinav/git/biodiv-api/conf/Config.yml"));
		// for (Map.Entry<String, Object> entry : list.entrySet()){
		// System.out.println(entry.getKey() + "/" + entry.getValue());
		// }
		// }catch(Exception e){
		// e.printStackTrace();
		// System.out.println(e);
		// }
		//
		// auto scanning of all classed for resources providers and features

		// register(new AbstractBinder() {
		// @Override
		// protected void configure() {
		// System.out.println("binding interceptor in abstractbinder");
		// bind(MyInterceptionService.class).to(org.glassfish.hk2.api.InterceptionService.class)
		// .in(Singleton.class);
		// }
		// });
		//

		register(new ContainerLifecycleListener() {

			@Override
			public void onStartup(Container container) {

				log.debug("Configuring GuiceBridge onStartup");

				ServletContainer servletContainer = (ServletContainer) container;

				Injector injector = (Injector) servletContainer.getServletContext()
						.getAttribute(Injector.class.getName());

				log.trace("==============================");
				log.trace(injector.getAllBindings().toString());
				log.trace("==============================");

				log.debug("Registering new resourceConfig");
				ResourceConfig newRC = new ResourceConfig();// container.getConfiguration();
				
				//PAC4j related bindings
				newRC.register(new JaxRsConfigProvider(injector.getInstance(Config.class)));
				//newRC.register(injector.getInstance(JaxRsContextFactoryProvider.class));
				newRC.register(injector.getInstance(ServletJaxRsContextFactoryProvider.class));
				newRC.register(injector.getInstance(Pac4JSecurityFeature.class));
				newRC.register(injector.getInstance(Pac4JValueFactoryProvider.Binder.class));
				
				
				
				newRC.register(new AbstractBinder() {
					@Override
					protected void configure() {
						log.debug("binding interceptor in abstractbinder");
						bind(MyInterceptionService.class).to(org.glassfish.hk2.api.InterceptionService.class)
								.in(Singleton.class);
						// bind(BiodivResponseFilter.class).to(BiodivResponseFilter.class).in(Singleton.class);
					}
				});

				newRC.packages("biodiv");
				//newRC.register(RolesAllowedDynamicFeature.class);

				newRC.register(org.glassfish.jersey.server.filter.UriConnegFilter.class);

				newRC.register(new LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
						Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY, Integer.MAX_VALUE));

				newRC.register(JTSObjectMapperProvider.class);
					
				
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				Hibernate5Module hibernate5Module = new Hibernate5Module();
				hibernate5Module.enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
				objectMapper.registerModule(hibernate5Module);

				// objectMapper.setVisibility(PropertyAccessor.ALL,
				// JsonAutoDetect.Visibility.ANY);
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				// objectMapper.configure(SerializationFeature.INDENT_OUTPUT,
				// true); //
				// Different from default so you can test it :)
				// objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

				
				// create JsonProvider to provide custom ObjectMapper
				JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
				provider.setMapper(objectMapper);

				newRC.register(provider);

				newRC.register(org.glassfish.jersey.jackson.JacksonFeature.class);
				newRC.register(biodiv.CustomLoggingFilter.class);
				
				newRC.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
				newRC.register(ValidationConfigurationContextResolver.class);

				log.debug("Reloading servletContainer with new resourceconfig");
				servletContainer.reload(newRC);

				log.debug("Initializing guicebridge with servicelocator");
				//InjectionManager im = container.getApplicationHandler().getInjectionManager();
				//ServiceLocator serviceLocator = im.getInstance(ServiceLocator.class);
				ServiceLocator serviceLocator = container.getApplicationHandler().getServiceLocator();

				GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

				GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);

				log.debug("Injecting guice injector into guicebridge");
				guiceBridge.bridgeGuiceInjector(injector);

			}

			@Override
			public void onShutdown(Container container) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReload(Container container) {
				// TODO Auto-generated method stub

			}
		});

		// System.out.println("Autoscanning packages for registering jaxrs
		// providers and features");
		//
		// packages("biodiv");
		//
		// System.out.println("Registering other features");
		// register(RolesAllowedDynamicFeature.class);
		//
		// register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
		//
		// register(new
		// LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
		// Level.ALL,
		// LoggingFeature.Verbosity.PAYLOAD_ANY, Integer.MAX_VALUE));
		//
		// // Object mapper for geometry
		// register(JTSObjectMapperProvider.class);
		//
		// // create custom ObjectMapper
		// ObjectMapper objectMapper = new ObjectMapper();
		// objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// Hibernate5Module hibernate5Module = new Hibernate5Module();
		// hibernate5Module.enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
		// objectMapper.registerModule(hibernate5Module);
		//
		// // objectMapper.setVisibility(PropertyAccessor.ALL,
		// // JsonAutoDetect.Visibility.ANY);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
		// false);
		// // objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		// //
		// // Different from default so you can test it :)
		// //
		// objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		//
		// // create JsonProvider to provide custom ObjectMapper
		// JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		// provider.setMapper(objectMapper);
		//
		// register(provider);
		//
		// register(org.glassfish.jersey.jackson.JacksonFeature.class);
		// register(biodiv.CustomLoggingFilter.class);
		// // optimization to disable scanning all packages for providers and
		// // features
		// // property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
	}

	public Map<String, Object> getGetConfig() {
		return getConfig;
	}

	public void setGetConfig(Map<String, Object> getConfig) {
		this.getConfig = getConfig;
	}
	/**
     * Custom configuration of validation. This configuration defines custom:
     * <ul>
     *     <li>ConstraintValidationFactory - so that validators are able to inject Jersey providers/resources.</li>
     *     <li>ParameterNameProvider - if method input parameters are invalid, this class returns actual parameter names
     *     instead of the default ones ({@code arg0, arg1, ..})</li>
     * </ul>
     */
    public static class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

        @Context
        private ResourceContext resourceContext;

        @Override
        public ValidationConfig getContext(final Class<?> type) {
            return new ValidationConfig()
                    .constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class))
                    .parameterNameProvider(new CustomParameterNameProvider());
        }

        /**
         * See ContactCardTest#testAddInvalidContact.
         */
        private class CustomParameterNameProvider implements ParameterNameProvider {

            private final ParameterNameProvider nameProvider;

            public CustomParameterNameProvider() {
                nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
            }

            @Override
            public List<String> getParameterNames(final Constructor<?> constructor) {
                return nameProvider.getParameterNames(constructor);
            }

            @Override
            public List<String> getParameterNames(final Method method) {
                // See ContactCardTest#testAddInvalidContact.
                //if ("addContact".equals(method.getName())) {
                //    return Arrays.asList("contact");
                //}
                return nameProvider.getParameterNames(method);
            }
        }
    }
}
