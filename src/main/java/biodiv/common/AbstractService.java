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
			this.dao.save(entity);
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
			T entity = (T) this.dao.findById(id);
			this.dao.delete(entity);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public T findById(Long id) {
		log.debug("findById " + id);
		try {
			T entity = (T) this.dao.findById(id);
			return entity;
		} catch (RuntimeException re) {
			log.error("findById failed", re);
			throw re;
		}
	}

	public List<T> findAll(int limit, int offset) {
		log.debug("findAll");
		try {
			List<T> entities = this.dao.findAll(limit, offset);
			return entities;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}
	
	public List<T> findAll() {
		
		log.debug("findAll");
		try {
			List<T> entities = this.dao.findAll();
			return entities;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}
	
	
	
}
