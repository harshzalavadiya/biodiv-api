package biodiv.common;

import java.io.Serializable;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractObject {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	protected SessionFactory sessionFactory;
	
	//private SessionFactory sessionFactory;
	
	//protected AbstractObject(SessionFactory sessionFactory) {
	//	this.sessionFactory = sessionFactory;		
	//}
	
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
