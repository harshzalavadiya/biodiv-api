package biodiv.customField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.activityFeed.ActivityFeed;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.common.AbstractService;
import biodiv.observation.Observation;
import biodiv.observation.ObservationService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.userGroup.UserGroup;

public class CustomFieldService extends AbstractService<CustomField> {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private CustomFieldDao customFieldDao;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ActivityFeedService activityFeedService;
	
	@Inject
	CustomFieldService(CustomFieldDao customFieldDao){
		super(customFieldDao);
		this.customFieldDao = customFieldDao;
	}

	public String updateInlineCf(String fieldValue, Long cfId, Long obvId, long userId,Set<UserGroup> obvUserGrps) {
	
		try{
			
			Date dateCreated = new Date();
			Date lastUpdated = dateCreated;
			CustomField cf = findById(cfId);
			if(cf == null){
				return "cf not found error";
			}else{
				UserGroup ugHavingGivenCf = cf.getUserGroup();
				//Observation obv = observationService.findById(obvId);
				//Set<UserGroup> obvUserGrps = obv.getUserGroups();
				
				if(obvUserGrps.contains(ugHavingGivenCf)){
					User user = userService.findById((Long)userId);
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("columnName", cf.fetchSqlColumnName(cf.getId(),true));
					map.put("columnValue", cf.fetchTypecastValue(fieldValue,cf).get("defaultValue"));
					map.put("obvId", obvId);
			
					Object oldValue = fetchValue(cf,obvId);
					updateRow(cf,map);
					Object newValue = fetchValue(cf,obvId);
					
					//activityFeed code
					if(oldValue != newValue){
						String activityDescription = cf.getName()+" : "+ map.get("columnValue");
						String description = activityDescription;
						Map<String, Object> afNew = activityFeedService.createMapforAf("Object",obvId,null,
								"species.participation.Observation","species.participation.Observation",obvId,
								"Custom field edited","Custom field edited",activityDescription,description,null,null,null,true,null,dateCreated,lastUpdated);
						
						activityFeedService.addActivityFeed(user, afNew, null,(String)afNew.get("rootHolderType"));
					}
					return "success";
				}else{
					return "This observation doesn't have the targeted custom field type";
				}	
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			
		}
			
	}

	private void updateRow(CustomField cf, Map<String, Object> map) {
		
		String query;
		try{
			if(isRowExist(CustomField.getTableNameForGroup(cf.getUserGroup().getId(),false),(Long) map.get("obvId"))){
				query = "update "+CustomField.getTableNameForGroup(cf.getUserGroup().getId(),true)+" set "+
						map.get("columnName")+" =:columnValue where observation_id =:obvId";
				customFieldDao.updateOrInsertRow(query,map,true);
			}else{
				query = "insert into "+CustomField.getTableNameForGroup(cf.getUserGroup().getId(),true)+"  ( observation_id, "+
						map.get("columnName")+ " ) "+ " values (:obvId, :columnValue)";
				customFieldDao.updateOrInsertRow(query,map,false);
			}
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}

	private boolean isRowExist(String tableName, Long obvId) {
		
		try{
			String query = "select count(*) from "+tableName+" where observation.id =:obvId";
			Long result = customFieldDao.isRowExist(query,obvId);
			if(result>0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}

	private Object fetchValue(CustomField cf, Long obvId) {
		try{
			if(obvId == null){
				return cf.getDefaultValue();
			}
			String query = "select cf1."+cf.fetchSqlColumnName(cf.getId(),true)+ " from "
					+CustomField.getTableNameForGroup(cf.getUserGroup().getId(),true)+" cf1 where cf1.observation_id =:obvId";
			//String query = "from "+CustomField.getTableNameForGroup(cf.getUserGroup().getId());
			Object result = customFieldDao.fetchValue(query,obvId);
			return result;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}

	public List<Map<String, Object>> getAllCustomfiedls(Long obvId,Set<UserGroup> obvUserGrps) {
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try{
			
			for(UserGroup ug : obvUserGrps){
				List<CustomField> allCustomFieldsinGroup = fetchCustomFieldsByGroup(ug);
				for(CustomField _cf : allCustomFieldsinGroup){
					Object value = fetchForDisplay(_cf,obvId);
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String,Object> author = new HashMap<String, Object>();
					author.put("id", _cf.getUser().getId());
					author.put("icon",_cf.getUser().getIcon());
					author.put("name", _cf.getUser().getName());
					map.put("id",_cf.getId());
					map.put("allowedMultiple",_cf.isAllowedMultiple());
					map.put("author",author);
					map.put("dataType",_cf.getDataType());
					map.put("defaultValue",_cf.getDefaultValue());
					map.put("displayOrder",_cf.getDisplayOrder());
					map.put("isMandatory",_cf.isIsMandatory());
					map.put("key",_cf.getName());
					map.put("notes",_cf.getNotes());
					map.put("options",_cf.getOptions());
					map.put("ugId",_cf.getUserGroup().getId());
					map.put("allowedParticipation",_cf.getAllowedParticipation());
					map.put("value", value);
					
					result.add(map);
				}
			}
			return result;
		}catch(Exception e){
			
		}finally{
			
		}
		return result;
		
	}

	private Object fetchForDisplay(CustomField cf, Long obvId) {
		
		try{
			Object value = fetchValue(cf,obvId);
			if( value != null && (cf.getDataType().equalsIgnoreCase("Date"))){
				//value = ((Object) value).format()
			}else if(value != null && cf.isAllowedMultiple()){
				//
			}
			return value;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	private List<CustomField> fetchCustomFieldsByGroup(UserGroup ug) {
		
		try{
			List<CustomField> cf = customFieldDao.fetchCustomFieldsByGroup(ug);
			return cf;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
}
