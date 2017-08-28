package biodiv.observation;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import biodiv.observation.Observation;
import biodiv.observation.ObservationDao;
import biodiv.util.HibernateUtil;

public class ObservationService {

	private static ObservationDao observationDao;

	public ObservationService() {
		observationDao = new ObservationDao();
	}

	public void persist(Observation entity) {
		observationDao.openCurrentSessionwithTransaction();
		observationDao.persist(entity);
		observationDao.closeCurrentSessionwithTransaction();
	}

	public void update(Observation entity) {
		observationDao.openCurrentSessionwithTransaction();
		observationDao.update(entity);
		observationDao.closeCurrentSessionwithTransaction();
	}

	public Observation findById(Long id) {
		observationDao.openCurrentSession();
		Observation Observation = observationDao.findById(id);
		observationDao.closeCurrentSession();
		return Observation;
	}

	public void delete(String id) {
		observationDao.openCurrentSessionwithTransaction();
		Observation Observation = observationDao.findById(Long.parseLong(id));
		observationDao.delete(Observation);
		observationDao.closeCurrentSessionwithTransaction();
	}

	public List<Observation> findAll(int limit, int offset) {
		observationDao.openCurrentSession();
		List<Observation> observations = observationDao.findAll(limit, offset);
		observationDao.closeCurrentSession();
		return observations;
	}

/*    public List<Observation> find(Map<String, String> params){
    	Session session = observationDao.openCurrentSession();
    	Map obvFindQuery = findObservationQuery(params);
    	String query = obvFindQuery.query + 
    	Query query = session.createQuery("from biodiv.observation.Observation");
        List<Observation> observations =  query.setMaxResults(2).list();
        System.out.println(observations);
		observationDao.closeCurrentSession();
		return observations;
    }
*/    
    
}
