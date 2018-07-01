package biodiv.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.NoResultException;

import javax.persistence.NoResultException;

public abstract class AbstractDao<T, K extends Serializable> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	protected SessionFactory sessionFactory;
	
	protected Class<? extends T> daoType;

	protected AbstractDao(SessionFactory sessionFactory) {
		log.trace("AbstractDao constructor");
		daoType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.sessionFactory = sessionFactory;
	}

	public void save(T entity) {
		sessionFactory.getCurrentSession().save(entity);
	}

	public void update(T entity) {
		sessionFactory.getCurrentSession().update(entity);
	}

	public void delete(T entity) {
		sessionFactory.getCurrentSession().delete(entity);
	}

	public abstract T findById(K id);

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return (List<T>) sessionFactory.getCurrentSession().createCriteria(daoType)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(int limit, int offset) {
		return (List<T>) sessionFactory.getCurrentSession().createCriteria(daoType)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setFirstResult(offset).setMaxResults(limit).list();
	}
	
	//TODO:improve this to do dynamic finder on any property
	public T findByPropertyWithCondition(String property, String value, String condition) {
		log.debug("findByPropertyWithCondition {}, {}", property, value);
		log.debug("daoType {} ", daoType);
		String queryStr = "" +
			    "from "+daoType.getSimpleName()+" t " +
			    "where t."+property+" "+condition+" :value" ;
		log.debug ("Running query : "+queryStr);
		org.hibernate.query.Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
		query.setParameter("value", value);
		
		T entity = null;
		try {
			entity = (T) query.getSingleResult();
		} catch(NoResultException e) {
			//e.printStackTrace();
			log.error("NoResultException {}", e.getMessage());
		}

		
		return entity;

	}

	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}
}
