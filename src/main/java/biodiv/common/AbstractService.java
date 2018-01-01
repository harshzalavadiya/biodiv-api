package biodiv.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class  AbstractService<T> {

	private static final Logger log = LoggerFactory.getLogger(AbstractService.class);
	public Class<T> entityClass;
	//protected AbstractDao<T, Long> dao;
	
	
	public abstract AbstractDao<T, Long> getDao();
	
	public AbstractService() {
		System.out.println("AbstractService constructor");
		
		//entityClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}
	

	/*public void setDao(AbstractDao<T,Long> dao) {
		this.dao = dao;
	}*/
	
	public void save(T entity) {
		log.debug("persisting " + entity.getClass() + " instance " + entity);
		try {
			//getDao().openCurrentSessionWithTransaction();
			//HibernateUtil.getSessionFactory().getCurrentSession().getTransaction();   		
			//HibernateUtil.getSessionFactory().getCurrentSession();
			getDao().save(entity);
			//getDao().closeCurrentSessionWithTransaction();
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void update(T entity)  {
		log.debug("upting " + entity.getClass() + " instance " + entity);
		try {
			getDao().update(entity);
			log.debug("update successful");
		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		}

	}

	public void delete(String id) {
		log.debug("deleting " + id);
		try {
			//getDao().openCurrentSessionWithTransaction();
			T entity = (T) getDao().findById(Long.parseLong(id));
			getDao().delete(entity);
			//getDao().closeCurrentSessionWithTransaction();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public T findById(Long id) {
		log.debug("findById " + id);
		try {
			//System.out.println("inside findbyid");
			//HibernateUtil.getSessionFactory().getCurrentSession();
			//getDao().openCurrentSession();
			T entity = (T) getDao().findById(id);
			//getDao().closeCurrentSession();
			
			return entity;
		} catch (RuntimeException re) {
			log.error("findById failed", re);
			throw re;
		}
	}

	public List<T> findAll(int limit, int offset) {
		log.debug("findAll");
		System.out.println("findALL");
		try {
			//getDao().openCurrentSession();
			List<T> entities = getDao().findAll(limit, offset);
			//getDao().closeCurrentSession();
			return entities;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}
	
	public List<T> findAll() {
		
		log.debug("findAll");
		try {
			//getDao().openCurrentSession();
			List<T> entities = getDao().findAll();
			//getDao().closeCurrentSession();
			return entities;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}
	
	
	
}
