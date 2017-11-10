package biodiv.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.util.HibernateUtil;

public abstract class AbstractDao<T, K extends Serializable> {

	private static final Logger log = LoggerFactory.getLogger(AbstractDao.class);

	private Session currentSession;

	private Transaction currentTransaction;

	protected Class<? extends T> daoType;

	protected AbstractDao() {
		daoType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}


	public Session openCurrentSession() {
		currentSession = HibernateUtil.getSessionFactory().openSession();
		return currentSession;
	}

	public Session openCurrentSessionWithTransaction() {
		currentSession = HibernateUtil.getSessionFactory().openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSession() {
		currentSession.close();
	}

	public void closeCurrentSessionWithTransaction() {
		currentTransaction.commit();
		currentSession.close();
		log.debug("committing current transaction and closing current session");
	}

	public Session getCurrentSession() {
		return currentSession;
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

	public void persist(T entity) {
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
		return (List<T>) getCurrentSession().createCriteria(daoType)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(int limit, int offset) {
		return (List<T>) getCurrentSession().createCriteria(daoType)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setFirstResult(offset).setMaxResults(limit).list();
	}

}
