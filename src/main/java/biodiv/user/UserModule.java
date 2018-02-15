package biodiv.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class UserModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void configureServlets() {
		log.debug("Configuring UserModule Servlets");
		bind(User.class);		
		bind(UserDao.class).in(Singleton.class);
		bind(UserService.class).in(Singleton.class);
		bind(UserController.class).in(Singleton.class);
		
		bind(Role.class);
		bind(RoleDao.class).in(Singleton.class);
		bind(RoleService.class).in(Singleton.class);
	}
}
