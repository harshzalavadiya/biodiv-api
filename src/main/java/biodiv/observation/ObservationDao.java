package biodiv.observation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;

public class ObservationDao extends AbstractDao implements DaoInterface<Observation, Long> {
	
	private static final Logger log = LoggerFactory.getLogger(Observation.class);

	public void persist(Observation entity) {
		log.debug("persisting Observation instance");
		try {
			getCurrentSession().save(entity);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void update(Observation entity) {
		log.debug("updating Observation instance");
		try {
			getCurrentSession().update(entity);
			log.debug("update successful");
		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		}
	}

	public void delete(Observation entity) {
		log.debug("deleting Observation instance");
		try {
			getCurrentSession().delete(entity);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}		
	}
	
	public Observation findById(Long id) {
		Observation obv = (Observation) getCurrentSession().get(Observation.class, id);
		return obv; 
	}

	

	@SuppressWarnings("unchecked")
	public List<Observation> findAll(int limit, int offset) {
		List<Observation> obvs = (List<Observation>) getCurrentSession().createQuery("from Observation")
				.setFirstResult(offset)
				.setMaxResults(limit)
				.list();
		
		return obvs;
	}


}
