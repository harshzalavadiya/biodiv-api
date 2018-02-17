/**
 * 
 */
package biodiv.user;

import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;


public class UserDao extends AbstractDao<User, Long> implements DaoInterface<User, Long>{
	
	public UserDao() {
		System.out.println("UserDao constructor");
	}
	
	@Override
	public User findById(Long id) {
		User entity = (User) getCurrentSession().get(User.class, id);
		return entity;
	}

	public User findByEmail(String email) {
		Query q = getCurrentSession().createQuery("from User where email=:email");
		q.setParameter("email", email);
		List<User> users = q.getResultList();
		if(users.size() == 1) return  users.get(0);
		else throw new NotFoundException("No user found with email : "+email);
	}
	
	public User findByEmailAndPassword(String email, String password) {
		Query q = getCurrentSession().createQuery("from User where email=:email and password=:password");
		q.setParameter("email", email);
		q.setParameter("password", password);
		List<User> users = q.getResultList();
		if(users.size() == 1) return  users.get(0);
		else throw new NotFoundException("No user found with email and password");
	}

}
