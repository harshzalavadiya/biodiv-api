package biodiv.observation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Intercept;
import biodiv.common.AbstractService;

import biodiv.common.SpeciesGroup;
import biodiv.speciesGroup.SpeciesGroupService;

import biodiv.customField.CustomFieldService;

import biodiv.userGroup.UserGroup;

@Service
public class ObservationService extends AbstractService<Observation> {

	CustomFieldService customFieldService =  new CustomFieldService();
	
	private ObservationDao observationDao;
	private SpeciesGroupService speciesGroupService;
	
	public ObservationService() {
		this.observationDao = new ObservationDao();
		this.speciesGroupService=new SpeciesGroupService();
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
			List<Map<String, Object>> results = observationDao.list();
		return results;
	}


	public String updateInlineCf(String fieldValue, Long cfId, Long obvId, long userId) {
		
		String msg;
		try{
			Observation obv = findById(obvId);
			Set<UserGroup> obvUserGrps = obv.getUserGroups();
			msg = customFieldService.updateInlineCf(fieldValue,cfId,obvId,userId,obvUserGrps);
			return msg;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}


	public List<Map<String, Object>> getCustomFields(Long obvId) {
		
		try{
			Observation obv = findById(obvId);
			Set<UserGroup> obvUserGrps = obv.getUserGroups();
			List<Map<String,Object>> cf = customFieldService.getAllCustomfiedls(obvId,obvUserGrps);
			return cf;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	public List<ObservationResource> getResouce(long id) {
		// TODO Auto-generated method stub
		List<ObservationResource> observationResources=observationDao.getResource(id);
		return observationResources;

	}


	public Observation updateGroup(Long objectid, Long groupid) {
		// TODO Auto-generated method stub
			
		SpeciesGroup speciesGroup =  speciesGroupService.findById(groupid);
		Observation obseravtion=show(objectid);
		return observationDao.updateGroup(obseravtion,speciesGroup);
	}


	public Observation show(long id) {
		Observation obseravtion=observationDao.findById(id);
		return obseravtion;
	}

}
