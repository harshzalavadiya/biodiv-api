package biodiv.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			System.out.println("Getting sessionFactory");
			try {
				// loads configuration and mappings
				Configuration configuration = new Configuration().configure();
				sessionFactory = configuration.buildSessionFactory();
				/*final ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
						.applySettings(configuration.getProperties()).build();

				// sessionFactory = new MetadataSources( standardRegistry
				// ).buildMetadata().buildSessionFactory();
				Metadata metadata = new MetadataSources(standardRegistry).addAnnotatedClass(User.class)
						.addAnnotatedClass(Observation.class).getMetadataBuilder()
						.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE).build();

				sessionFactory = metadata.getSessionFactoryBuilder()
						// .applyBeanManager( getBeanManager() )
						.build();
				*/
			} catch (Exception e) {
				e.printStackTrace();
				if (sessionFactory != null)
					sessionFactory.close();
				// StandardServiceRegistryBuilder.destroy(standardRegistry);

			}
		}
		return sessionFactory;
	}

	public static Session getSession() {
		Session session = getSessionFactory().openSession();
		return session;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
	
	

}
