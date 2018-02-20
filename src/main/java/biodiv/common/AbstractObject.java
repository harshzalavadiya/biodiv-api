package biodiv.common;

import java.io.Serializable;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

public class AbstractObject {

	@Inject
	private SessionFactory sessionFactory;
	
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
