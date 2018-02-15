package biodiv.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class  AbstractService<T> {

	private static final Logger log = LoggerFactory.getLogger(AbstractService.class);
	public Class<T> entityClass;
	private  AbstractDao<T, Long> dao;
	
	public AbstractService(AbstractDao<T, Long> dao) {
		System.out.println("\nAbstractService constructor");
		this.dao = dao;
		//entityClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	public void save(T entity) {
		log.debug("persisting " + entity.getClass() + " instance " + entity);
		try {
			//this.dao.openCurrentSessionWithTransaction();
			//HibernateUtil.getSessionFactory().getCurrentSession().getTransaction();   		
			//HibernateUtil.getSessionFactory().getCurrentSession();
			this.dao.save(entity);
			//this.dao.closeCurrentSessionWithTransaction();
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void update(T entity)  {
		log.debug("upting " + entity.getClass() + " instance " + entity);
		try {
			this.dao.update(entity);
			log.debug("update successful");
		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		}

	}

	public void delete(Long id) {
		log.debug("deleting " + id);
		try {
			//this.dao.openCurrentSessionWithTransaction();
			T entity = (T) this.dao.findById(id);
			this.dao.delete(entity);
			//this.dao.closeCurrentSessionWithTransaction();
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
			//this.dao.openCurrentSession();
			T entity = (T) this.dao.findById(id);
			//this.dao.closeCurrentSession();
			
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
			//this.dao.openCurrentSession();
			List<T> entities = this.dao.findAll(limit, offset);
			//this.dao.closeCurrentSession();
			return entities;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}
	
	public List<T> findAll() {
		
		log.debug("findAll");
		try {
			//this.dao.openCurrentSession();
			List<T> entities = this.dao.findAll();
			//this.dao.closeCurrentSession();
			return entities;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}
	
	
	
}
