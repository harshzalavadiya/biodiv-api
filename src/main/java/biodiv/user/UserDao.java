/**
 * 
 */
package biodiv.user;

import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;


class UserDao extends AbstractDao implements DaoInterface<User, Long>{
	
	public void persist(User entity) {
		getCurrentSession().save(entity);
	}

	public void update(User entity) {
		getCurrentSession().update(entity);
	}

	public User findById(Long id) {
		User user = (User) getCurrentSession().get(User.class, id);
		return user; 
	}

	public void delete(User entity) {
		getCurrentSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	public List<User> findAll(int limit, int offset) {
		List<User> users = (List<User>) getCurrentSession().createQuery("from User")
				.setFirstResult(offset)
				.setMaxResults(limit)
				.list();
		
	        System.out.println(users);
		return users;
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
