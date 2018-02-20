package biodiv;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
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
		return Guice.createInjector(new ServletModule() {

			@Override
			protected void configureServlets() {
				log.debug("Configuring Servlets");

				// Loading external configuration
				Configurations configs = new Configurations();
				Configuration config = null;
				try {
					String ENV_NAME = "BIODIV_API_CONFIG";
					log.info("Reading configuration from : {}", System.getenv(ENV_NAME));
					config = configs.properties(new File(System.getenv(ENV_NAME)));
					bind(Configuration.class).toInstance(config);
				} catch (ConfigurationException cex) {
					cex.printStackTrace();
				}

				// Configuring hibernate session factory
				try {
					// loads configuration and mappings
					org.hibernate.cfg.Configuration hibConf = new org.hibernate.cfg.Configuration();
					Properties dbProps = new Properties();
					dbProps.setProperty("hibernate.connection.url", config.getString("db.url"));
					dbProps.setProperty("hibernate.connection.username", config.getString("db.username"));
					dbProps.setProperty("hibernate.connection.password", config.getString("db.password"));
					hibConf.configure("hibernate.cfg.xml").addProperties(dbProps);
					for (Class cls : getEntityClassesFromPackage("biodiv")) {
						log.trace("Adding annotated class : {}", cls);
						hibConf.addAnnotatedClass(cls);
					}
					ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
							.applySettings(hibConf.getProperties()).build();

					// URL configFileURL = getResource(); //some method to get
					// hold of the location of your hibernate.cfg.xml
					// StandardServiceRegistry standardRegistry = new
					// StandardServiceRegistryBuilder().configure(configFileURL).build();
					Metadata metaData = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
					Collection<PersistentClass> entityBindings = metaData.getEntityBindings();
					log.trace("All entity bindings : {}", entityBindings);
					Iterator<PersistentClass> iterator = entityBindings.iterator();
					while (iterator.hasNext()) {
						PersistentClass persistentClass = iterator.next();
						System.out.println(persistentClass);
					}

					SessionFactory sessionFactory = hibConf.buildSessionFactory(serviceRegistry);

					bind(SessionFactory.class).toInstance(sessionFactory);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// rest("/*").packages("biodiv");

				// INTERCEPTOR
				ResourceInterceptor interceptor = new ResourceInterceptor();
				bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), interceptor);
				bind(ResourceInterceptor.class).toInstance(interceptor);
				requestInjection(interceptor);

				// FILTERS
				// bind(BiodivResponseFilter.class).in(Singleton.class);

			}
		}, new BiodivCommonModule(), new ActivityFeedModule(), new AuthModule(), new CommentModule(),
				new CustomFieldModule(), new DatasetModule(), new FollowModule(), new MapModule(),
				new ObservationModule(), new TaxonModule(), new TraitModule(), new UserModule(), new UserGroupModule());
	}

	private static List<Class<?>> getEntityClassesFromPackage(String packageName)
			throws ClassNotFoundException, IOException, URISyntaxException {
		List<String> classNames = getClassNamesFromPackage(packageName);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : classNames) {
			Class<?> cls = Class.forName(className);
			Annotation[] annotations = cls.getAnnotations();

			for (Annotation annotation : annotations) {
				if (annotation instanceof javax.persistence.Entity) {
					System.out.println("Mapping Entity : " + cls.getCanonicalName());
					classes.add(cls);
				}
			}
		}

		return classes;
	}

	private static ArrayList<String> getClassNamesFromPackage(final String packageName)
			throws IOException, URISyntaxException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ArrayList<String> names = new ArrayList<String>();

		URL packageURL = classLoader.getResource(packageName);

		URI uri = new URI(packageURL.toString());
		File folder = new File(uri.getPath());

		Files.find(Paths.get(folder.getAbsolutePath()), 999, (p, bfa) -> bfa.isRegularFile()).forEach(file -> {
			String name = file.toFile().getAbsolutePath().replaceAll(folder.getAbsolutePath() + '/', "").replace('/',
					'.');
			if (name.indexOf('.') != -1) {
				name = packageName + '.' + name.substring(0, name.lastIndexOf('.'));
				names.add(name);
			}
		});

		return names;
	}

}
