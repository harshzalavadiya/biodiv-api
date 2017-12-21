package biodiv.observation;

import java.util.List;
import java.util.Map;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Intercept;
import biodiv.common.AbstractService;
import biodiv.userGroup.UserGroup;

@Service
public class ObservationService extends AbstractService<Observation> {


	private static final Logger log = LoggerFactory.getLogger(ObservationService.class);

	private ObservationDao observationDao;
	
	public ObservationService() {
		this.observationDao = new ObservationDao();
	}
	
	
	@Override
	public ObservationDao getDao() {
		return observationDao;
	}

	public List<Observation> findAllByGroup(int max, int offset) {
		System.out.println("alter");
		return null;
	}

	@Intercept
	public List<UserGroup> obvUserGroups(long id) {
		try{
			List<UserGroup> usrGrps = observationDao.obvUserGroups(id);
			return usrGrps;
		} catch(Exception e) {
			throw e;
		} finally{
		}

	
	}


	public List<Map<String, Object>> list() {
			System.out.println("service mai  aaye");
			List<Map<String, Object>> results = observationDao.list();
		return results;
	}

}
