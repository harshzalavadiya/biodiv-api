package biodiv.customField;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
import joptsimple.util.DateConverter;
import net.minidev.json.JSONObject;

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

	public List<Object> updateInlineCf(String fieldValue, Long cfId, Long obvId, long userId,Set<UserGroup> obvUserGrps,Long loggedInUserId,
			Long obvAuthorId,Boolean isAdmin) {
	
		try{
			
			Date dateCreated = new Date();
			Date lastUpdated = dateCreated;
			CustomField cf = findById(cfId);
			List<Object> toReturn =  new ArrayList<Object>();
			String msg = null;
			if(cf == null){
				msg =  "cf not found error";
				toReturn.add(msg);
				toReturn.add(null);
				return toReturn;
			}else{
				
				if(cf.getAllowedParticipation() || (!cf.getAllowedParticipation() && (loggedInUserId.equals(obvAuthorId) || isAdmin))){
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
						System.out.println("new value of cf "+newValue);
						JSONObject cfObject = new JSONObject();
						cfObject.put("key", cf.getName());
						cfObject.put("value", convertForElastic(newValue,cf));
						msg = "success";
						toReturn.add(msg);
						toReturn.add(cfObject);
						return toReturn;
					}else{
						msg = "This observation doesn't have the targeted custom field type";
						toReturn.add(msg);
						toReturn.add(null);
						return toReturn;
					}	
				}else{
					msg = "You don't have permission to edit this Custom Field";
					toReturn.add(msg);
					toReturn.add(null);
					return toReturn;
				}
				
				
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			
		}
			
	}

	private Object convertForElastic(Object newValue,CustomField cf) {
		
		Object value = newValue;
		
		if(cf.getDataType().equalsIgnoreCase("DATE")){
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String input = inputFormat.format(newValue);
			value = input;
      		System.out.println("input "+input);
//			DateFormat outputFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			
//			Date date;
//			try {
//				date = outputFormat.parse( input);
//				System.out.println("output "+date);
//				value = date; 
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		if(cf.getDataType().equalsIgnoreCase("TEXT") && cf.isAllowedMultiple()){
			String[] allValues = ((String) newValue).split(",");
			value = allValues;
		}
		
		return value;
	}

	private void updateRow(CustomField cf, Map<String, Object> map) {
		
		String query;
		try{
			if(isRowExist(CustomField.getTableNameForGroup(cf.getUserGroup().getId(),true),(Long) map.get("obvId"))){
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
			String query = "select count(*) from "+tableName+" where observation_id =:obvId";
			BigInteger result = customFieldDao.isRowExist(query,obvId);
			if(result.compareTo(BigInteger.ZERO) == 1){
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

	public List<CustomField> fetchCustomFieldsByGroup(UserGroup ug) {
		
		try{
			List<CustomField> cf = customFieldDao.fetchCustomFieldsByGroup(ug);
			return cf;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	public List<CustomField> fetchAllCustomFields() {
		try{
			List<CustomField> cf = customFieldDao.fetchAllCustomFields();
			return cf;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	
	
}
