package biodiv.user;

import java.util.List;

public class UserService {
	
	private static UserDao userDao;

	public UserService() {
		userDao = new UserDao();
	}

	public void persist(User entity) {
		userDao.openCurrentSessionwithTransaction();
		userDao.persist(entity);
		userDao.closeCurrentSessionwithTransaction();
	}

	public void update(User entity) {
		userDao.openCurrentSessionwithTransaction();
		userDao.update(entity);
		userDao.closeCurrentSessionwithTransaction();
	}

	public User findById(Long id) {
		userDao.openCurrentSession();
		User User = userDao.findById(id);
		userDao.closeCurrentSession();
		return User;
	}

	public void delete(String id) {
		userDao.openCurrentSessionwithTransaction();
		User User = userDao.findById(Long.parseLong(id));
		userDao.delete(User);
		userDao.closeCurrentSessionwithTransaction();
	}

	public List<User> findAll(int limit, int offset) {
		userDao.openCurrentSession();
		List<User> users = userDao.findAll(limit, offset);
		userDao.closeCurrentSession();
		return users;
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
