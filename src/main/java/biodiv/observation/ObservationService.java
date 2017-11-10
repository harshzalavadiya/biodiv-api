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
    
}
