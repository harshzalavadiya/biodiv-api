package biodiv.observation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Intercept;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.common.AbstractService;

import biodiv.common.SpeciesGroup;
import biodiv.speciesGroup.SpeciesGroupService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.customField.CustomFieldService;

import biodiv.userGroup.UserGroup;

@Service
public class ObservationService extends AbstractService<Observation> {

	CustomFieldService customFieldService =  new CustomFieldService();
	
	private ObservationDao observationDao;
	private SpeciesGroupService speciesGroupService;
	ActivityFeedService activityFeedService ;
	UserService userService;
	
	public ObservationService() {
		this.observationDao = new ObservationDao();
		this.speciesGroupService=new SpeciesGroupService();
		activityFeedService = new ActivityFeedService();
		userService = new UserService();
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


	public Object updateGroup(Long objectid, Long newgroupid,Long oldGroupId,Long userId) {
		// TODO Auto-generated method stub
		User user = userService.findById(userId);
		Object obj;
		SpeciesGroup speciesGroup =  speciesGroupService.findById(newgroupid);
		String newSpeciesGroupName = speciesGroup.getName();
		SpeciesGroup oldSpeciesGroup = speciesGroupService.findById(oldGroupId);
		String oldSpeciesGroupName = oldSpeciesGroup.getName();
		Observation obseravtion=show(objectid);
		obj =  observationDao.updateGroup(obseravtion,speciesGroup);
		
		//activityFeed
		
		String activityDescription = oldSpeciesGroupName+" to "+newSpeciesGroupName;
		System.out.println(activityDescription);
		Map<String, Object> afNew = activityFeedService.createMapforAf("Object",objectid,obseravtion,
				"species.participation.Observation","species.participation.Observation",objectid,"Observation species group updated",
				"Species group updated",activityDescription,activityDescription,null,null,true,null);
		activityFeedService.addActivityFeed(user,afNew,obseravtion,(String)afNew.get("rootHolderType"));
		//activityFeed
		return obj;
	}


	public Observation show(long id) {
		Observation obseravtion=observationDao.findById(id);
		return obseravtion;
	}

}
