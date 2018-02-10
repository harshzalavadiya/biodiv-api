package biodiv.user;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.register.RegistrationCode;
import biodiv.common.AbstractService;

public class UserService extends AbstractService<User> {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private UserDao userDao;

	public UserService() {
		this.userDao = new UserDao();
	}

	@Override
	public UserDao getDao() {	
		return userDao;
	}
	
	public User findByEmail(String email) {
		try {
			userDao.openCurrentSession();
			User user = userDao.findByPropertyWithCondition("email", email, "=");
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			userDao.closeCurrentSession();
		}
	}

	public User findByEmailAndPassword(String email, String password) {
		try {
			userDao.openCurrentSession();
			User user = userDao.findByEmailAndPassword(email, password);
			return user;
		} catch (Exception e) {
			throw e;
		} finally {
			userDao.closeCurrentSession();
		}
	}

	public RegistrationCode register(String email) {
		if (email == null)
			return null;
		try {
			RegistrationCode registrationCode = new RegistrationCode(email);
			userDao.openCurrentSessionWithTransaction();
			if (registrationCode.save() == null) {
				log.error("Coudn't save registrationCode");
			}
			return registrationCode;
		} catch (Exception e) {
			throw e;
		} finally {
			userDao.closeCurrentSessionWithTransaction();
		}
	}

	public Map<String,Object> findAuthorSignature(User user) {
			Map<String,Object> author = new HashMap<String,Object>();
			author.put("id", user.getId());
			author.put("icon", user.getIcon());
			author.put("name",user.getName());
			return author;
	}

}
