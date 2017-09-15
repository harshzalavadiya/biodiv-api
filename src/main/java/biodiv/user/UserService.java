package biodiv.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;

public class UserService extends AbstractService<User> {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private UserDao userDao;
	
	public UserService() {
		this.userDao = new UserDao();		
	}
	
	public UserDao getDao() {
		return userDao;
	}

	public User findByEmail(String email) {
		userDao.openCurrentSession();
		User user = userDao.findByEmail(email);
		userDao.closeCurrentSession();
		return user;
	}
	
	public User findByEmailAndPassword(String email, String password) {
		userDao.openCurrentSession();
		User user = userDao.findByEmailAndPassword(email, password);
		userDao.closeCurrentSession();
		return user;
	}

}
