package biodiv.common;

import java.io.Serializable;

import biodiv.util.HibernateUtil;

public class AbstractObject {

	/**
	 * returns generated id. It can be long or string in serializable format 
	 * @return
	 * dummy
	 */
	public Serializable save() {
		try {
			return HibernateUtil.getSessionFactory().getCurrentSession().save(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
