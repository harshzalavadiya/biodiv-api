package biodiv.common;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractObject {

	private final Logger log = LoggerFactory.getLogger(getClass());

//	protected AbstractObject(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;		
//	}
	
	/**
	 * returns generated id. It can be long or string in serializable format 
	 * @return
	 * dummy
	 */
	public Serializable save(SessionFactory sessionFactory) {
		try {
			return sessionFactory.getCurrentSession().save(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void update(SessionFactory sessionFactory) {
		sessionFactory.getCurrentSession().update(this);
	}

	public void delete(SessionFactory sessionFactory) {
		sessionFactory.getCurrentSession().delete(this);
	}

	public static Object load(Serializable id, Class class_, SessionFactory sessionFactory) {
		return sessionFactory.getCurrentSession().load(class_, id);
	}
	
	public static Object get(Serializable id, Class class_, SessionFactory sessionFactory) {
		return sessionFactory.getCurrentSession().load(class_, id);
	}

}
