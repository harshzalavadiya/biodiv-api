package biodiv.common;

import javax.persistence.MappedSuperclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import biodiv.Transactional;
import biodiv.userGroup.UserGroupModel;
import biodiv.util.HibernateUtil;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DataObject<T> extends ParticipationMetadata implements UserGroupModel, java.io.Serializable {

	public Class<T> entityClass;

	private static final Logger log = LoggerFactory.getLogger(DataObject.class);

	// private static SessionFactory sf = HibernateUtil.getSessionFactory();
	public DataObject() {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public DataObject get(long Id) {
		System.out.println("DataObject class: " + this.getClass());
		try {
			DataObject instance = (DataObject) HibernateUtil.getSessionFactory().getCurrentSession()
					.get(this.getClass(), Id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get  failed", re);
			throw re;
		}

	}

	@Override
	public T read(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T load(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(boolean flush) {

		System.out.println("Object to update : " + this);
		try {
			HibernateUtil.getSessionFactory().getCurrentSession().update(this);
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
			HibernateUtil.getSessionFactory().getCurrentSession().delete(this);
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
			HibernateUtil.getSessionFactory().getCurrentSession().save(this);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save  failed", re);
			throw re;
		}

	}

}