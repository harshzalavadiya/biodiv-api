package biodiv.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

class ObservationDao extends AbstractDao<Observation, Long> implements DaoInterface<Observation, Long> {
	
	private static final Logger log = LoggerFactory.getLogger(ObservationDao.class);

	@Override
	public Observation findById(Long id) {
		Observation entity = (Observation) getCurrentSession().get(Observation.class, id);
		return entity;
	}
	/*
	public List<Observation> findAll(int limit, int offset) {
		List<Observation> obvs = (List<Observation>) getCurrentSession().createQuery("from Observation")
				.setFirstResult(offset)
				.setMaxResults(limit)
				.list();
		
		return obvs;
	}
*/

}
