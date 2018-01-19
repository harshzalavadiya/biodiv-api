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


	

	private ObservationDao observationDao;
	
	public ObservationService() {
		System.out.println("ObservationService constructor");
		this.observationDao = new ObservationDao();
		System.out.println("ObservationService constructor end");
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


	public List<ObservationResource> getResouce(long id) {
		// TODO Auto-generated method stub
		List<ObservationResource> observationResources=observationDao.getResource(id);
		return observationResources;
	}

}
