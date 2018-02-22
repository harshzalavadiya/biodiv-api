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

public abstract class AbstractDao<T, K extends Serializable> {

	private static final Logger log = LoggerFactory.getLogger(AbstractDao.class);

	private Session currentSession;
	
	@Inject
	private SessionFactory sessionFactory;
	
	private Transaction currentTransaction;

	protected Class<? extends T> daoType;

	protected AbstractDao() {
		log.trace("AbstractDao constructor");
		daoType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}


	public Session openCurrentSession() {		
		currentSession = sessionFactory.openSession();
		return currentSession;
	}

	public Session openCurrentSessionWithTransaction() {
		currentSession = sessionFactory.openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSession() {
		//sessionFactory.getCurrentSession().close();
	}

	public void closeCurrentSessionWithTransaction() {
		currentTransaction.commit();
		currentSession.close();
		log.debug("committing current transaction and closing current session");
	}

	public Session getCurrentSession() {
		//System.out.println(System.identityHashCode(sessionFactory.getCurrentSession()));
		return sessionFactory.getCurrentSession();
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public Transaction getCurrentTransaction() {
		return currentTransaction;
	}

	public void setCurrentTransaction(Transaction currentTransaction) {
		this.currentTransaction = currentTransaction;
	}

	public void save(T entity) {
		getCurrentSession().save(entity);
	}

	public void update(T entity) {
		getCurrentSession().update(entity);
	}

	public void delete(T entity) {
		getCurrentSession().delete(entity);
	}

	public abstract T findById(K id);

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		System.out.println("findalllll");
		return (List<T>) getCurrentSession().createCriteria(daoType)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(int limit, int offset) {
		System.out.println("findalllllaa");
		return (List<T>) getCurrentSession().createCriteria(daoType)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setFirstResult(offset).setMaxResults(limit).list();
	}
	
	//TODO:improve this to do dynamic finder on any property
	public T findByPropertyWithCondition(String property, String value, String condition) {
		
		String queryStr = "" +
			    "from "+daoType.getSimpleName()+" t " +
			    "where t."+property+" "+condition+" :value" ;
		log.debug ("Running query : "+queryStr);
		org.hibernate.query.Query query = getCurrentSession().createQuery(queryStr);
		query.setParameter("value", value);
		
		T entity = (T) query.getSingleResult();
		
		return entity;

	}

}
