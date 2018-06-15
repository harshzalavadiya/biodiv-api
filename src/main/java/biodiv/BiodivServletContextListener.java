package biodiv;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import biodiv.Checklists.ChecklistsModule;
import biodiv.activityFeed.ActivityFeedModule;
import biodiv.admin.AdminModule;
import biodiv.allsearch.AllSearchModule;
import biodiv.auth.AuthModule;
import biodiv.comment.CommentModule;
import biodiv.common.BiodivCommonModule;
import biodiv.customField.CustomFieldModule;
import biodiv.dataTable.DataTableModule;
import biodiv.dataset.DatasetModule;
import biodiv.esclient.ElasticSearchClient;
import biodiv.follow.FollowModule;
import biodiv.mail.MailModule;
import biodiv.maps.MapModule;
import biodiv.observation.ObservationModule;
import biodiv.scheduler.SchedulerModule;
import biodiv.species.AcceptedSynonymModule;
import biodiv.speciesPermission.SpeciesPermissionModule;
import biodiv.taxon.TaxonModule;
import biodiv.taxon.search.SearchTaxonModule;
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
					
					   // Load the JDBC driver.
				    
				    
					// loads configuration and mappings
					org.hibernate.cfg.Configuration hibConf = new org.hibernate.cfg.Configuration();
					
					Properties dbProps = new Properties();
					
					dbProps.setProperty("hibernate.hikari.dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
					dbProps.setProperty("hibernate.hikari.dataSource.url", config.getString("db.url"));
					dbProps.setProperty("hibernate.hikari.dataSource.user", config.getString("db.username"));
					dbProps.setProperty("hibernate.hikari.dataSource.password", config.getString("db.password"));

					dbProps.setProperty("hibernate.dialect", "org.hibernate.spatial.dialect.postgis.PostgisPG93Dialect");					
					
					dbProps.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");

					// Hikari configurations
					dbProps.setProperty("hibernate.connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider");
					dbProps.setProperty("hibernate.hikari.maximumPoolSize" ,"20");
					dbProps.setProperty("hibernate.hikari.idleTimeout", "30000");
					
					dbProps.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
					dbProps.setProperty("hbm2ddl.auto", "update");
					dbProps.setProperty("hibernate.id.new_generator_mappings", "false");
					dbProps.setProperty("hibernate.transaction.coordinator_class", "jdbc");
					dbProps.setProperty("hibernate.current_session_context_class", "thread");
					dbProps.setProperty("hibernate.jdbc.batch_size", "50");
					
					dbProps.setProperty("show_sql", "true");

					dbProps.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
					dbProps.setProperty(Environment.USE_QUERY_CACHE, "true");
					dbProps.setProperty(Environment.CACHE_REGION_FACTORY, BiodivRedisRegionFactory.class.getName());
					dbProps.setProperty(Environment.CACHE_REGION_PREFIX, "hibernate");

					// Optional setting for second level cache statistics
					dbProps.setProperty(Environment.GENERATE_STATISTICS, "true");
					dbProps.setProperty(Environment.USE_STRUCTURED_CACHE, "true");

					dbProps.setProperty(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

					hibConf.addProperties(dbProps);
					for (Class<?> cls : getEntityClassesFromPackage("biodiv")) {
						log.trace("Adding annotated class : {}", cls);
						hibConf.addAnnotatedClass(cls);
					}
					ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
							.applySettings(hibConf.getProperties()).build();

			
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
					bind(ServiceRegistry.class).toInstance(serviceRegistry);
					bind(BiodivResponseFilter.class);

				} catch (Exception e) {
					e.printStackTrace();
				}

				//rest("/*").packages("biodiv");

				RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(HttpHost.create(config.getString("es.url"))));
				bind(RestHighLevelClient.class).toInstance(restHighLevelClient);

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
				new DataTableModule(), new ObservationModule(), new TaxonModule(), new TraitModule(), new UserModule(), 
				new UserGroupModule(), new AdminModule(), new SchedulerModule(), new MailModule(),
				new SpeciesPermissionModule(), new AllSearchModule(), new SearchTaxonModule(),new ChecklistsModule(),
				new AcceptedSynonymModule());
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

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Injector injector = (Injector) sce.getServletContext()
                 .getAttribute(Injector.class.getName());

		ElasticSearchClient elasticSearchClient = injector.getInstance(ElasticSearchClient.class);
		if(elasticSearchClient != null) {
			try {
				elasticSearchClient.close();
			} catch (IOException e) {
				log.error("Error closing elasticsearch client");
				e.printStackTrace();
			}
		}

		ServiceRegistry serviceRegistry = injector.getInstance(ServiceRegistry.class);
		if (serviceRegistry != null) {
			StandardServiceRegistryBuilder.destroy(serviceRegistry);
		}
	
		SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
/*		if(sessionFactory instanceof SessionFactoryImpl) {
		      SessionFactoryImpl sf = (SessionFactoryImpl)sessionFactory;
		      ConnectionProvider conn = sf.getConnectionProvider();
		      if(conn instanceof C3P0ConnectionProvider) { 
		        ((C3P0ConnectionProvider)conn).stop(); 
		      }
		   }
*/	
		sessionFactory.close();

		 
		super.contextDestroyed(sce);
		// ... First close any background tasks which may be using the DB ...
	    // ... Then close any DB connection pools ...

	    // Now deregister JDBC drivers in this context's ClassLoader:
	    // Get the webapp's ClassLoader
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    // Loop through all drivers
	    Enumeration<Driver> drivers = DriverManager.getDrivers();
	    while (drivers.hasMoreElements()) {
	        Driver driver = drivers.nextElement();
	        if (driver.getClass().getClassLoader() == cl) {
	            // This driver was registered by the webapp's ClassLoader, so deregister it:
	            try {
	                log.info("Deregistering JDBC driver {}", driver);
	                DriverManager.deregisterDriver(driver);
	            } catch (SQLException ex) {
	                log.error("Error deregistering JDBC driver {}", driver, ex);
	            }
	        } else {
	            // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
	            log.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
	        }
	    }
	    
	}

}
