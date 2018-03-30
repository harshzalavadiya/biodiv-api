package biodiv.common;

import java.io.Serializable;

import org.hibernate.SessionFactory;

public class AbstractObject {

	private SessionFactory sessionFactory;
	
	protected AbstractObject(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;		
	}
	
	/**
	 * returns generated id. It can be long or string in serializable format 
	 * @return
	 * dummy
	 */
	public Serializable save() {
		try {
			return sessionFactory.getCurrentSession().save(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
