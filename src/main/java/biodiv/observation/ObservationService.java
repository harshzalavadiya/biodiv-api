package biodiv.observation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;

public class ObservationService extends AbstractService<Observation> {


	private static final Logger log = LoggerFactory.getLogger(ObservationService.class);

	private ObservationDao observationDao;
	
	public ObservationService() {
		this.observationDao = new ObservationDao();		
	}
	
	public ObservationDao getDao() {
		return observationDao;
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
