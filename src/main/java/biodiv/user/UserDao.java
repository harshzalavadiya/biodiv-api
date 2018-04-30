/**
 * 
 */
package biodiv.user;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;


public class UserDao extends AbstractDao<User, Long> implements DaoInterface<User, Long>{

	@Inject
	public UserDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public User findById(Long id) {
		User entity = (User) sessionFactory.getCurrentSession().get(User.class, id);
		return entity;
	}

	public User findByEmail(String email) throws NotFoundException {
		Query q = sessionFactory.getCurrentSession().createQuery("from User where email=:email");
		q.setParameter("email", email);
		User user = null;
        try {
            user = (User) q.getSingleResult();
        } catch(NoResultException e) {
            e.printStackTrace(); 
            throw new NotFoundException(e);
        }
        return user;
	}
	
	public User findByEmailAndPassword(String email, String password)  throws NotFoundException {
		Query q = sessionFactory.getCurrentSession().createQuery("from User where email=:email and password=:password");
		q.setParameter("email", email);
		q.setParameter("password", password);
		List<User> users = q.getResultList();
		if(users.size() == 1) return  users.get(0);
		else throw new NotFoundException("No user found with email and password");
	}

}
