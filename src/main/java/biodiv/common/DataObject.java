package biodiv.common;

import javax.inject.Inject;
import javax.persistence.MappedSuperclass;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import biodiv.Transactional;
import biodiv.userGroup.UserGroupModel;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DataObject extends ParticipationMetadata implements UserGroupModel, java.io.Serializable {

	//public Class<T> entityClass;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private SessionFactory sessionFactory;
	
	public DataObject() {

	}

	@Override
	public DataObject get(long id, AbstractService<DataObject> abstractService) {
		System.out.println("DataObject class: " + this.getClass());
		try {
			DataObject instance = (DataObject) sessionFactory.getCurrentSession()
					.get(this.getClass(), id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get  failed : {}", re.getMessage());
			throw re;
		}

	}

	@Override
	public DataObject read(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataObject load(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(boolean flush) {

		System.out.println("Object to update : " + this);
		try {
			sessionFactory.getCurrentSession().update(this);
			log.debug("update  successful");
		} catch (RuntimeException re) {
			log.error("update  failed", re);
			throw re;
		}

	}

	@Override
	public void delete() {
		System.out.println("Object to delete : " + this);
		try {
			sessionFactory.getCurrentSession().delete(this);
			log.debug("delete  successful");
		} catch (RuntimeException re) {
			log.error("delete  failed", re);
			throw re;
		}

	}

	@Override
	@Transactional
	public void save() {
		System.out.println("Object to save : " + this);
		try {
			sessionFactory.getCurrentSession().save(this);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save  failed", re);
			throw re;
		}

	}

}