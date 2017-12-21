package biodiv.userGroup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

import biodiv.Intercept;
import biodiv.common.AbstractService;
import biodiv.common.DataObject;
import biodiv.observation.Observation;
import biodiv.observation.ObservationService;
import biodiv.user.User;
import biodiv.util.HibernateUtil;

@Service
public class UserGroupService extends AbstractService<UserGroup> {

	
	@Inject
	ObservationService observationService;
	
	private UserGroupDao userGroupDao;

	public UserGroupService() {
		this.userGroupDao = new UserGroupDao();
	}

	@Override
	public UserGroupDao getDao() {
		
		return userGroupDao;
	}

	@Intercept
	public List<UserGroup> userUserGroups(long userId) {
		
		
		//boolean localTransaction = false;
		try{
//			if((HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().getStatus()) != TransactionStatus.ACTIVE)
//			{
//				HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
//				localTransaction = true;
//			}
			List<UserGroup> usrGrp = getDao().userUserGroups(userId);
//			if(localTransaction == true)
//			{
//				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit(); 
//			}
			return usrGrp;
		} catch (Exception e){
//			if(localTransaction == true)
//			{
//				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback(); 
//			}  
			throw e;
		} finally{
			
		}	
	}
	
	public List<User> userList(long groupId,long roleId){
		try{
			getDao().openCurrentSession();
			List<User> usr = getDao().userList(groupId,roleId);
			return usr;
		} catch (Exception e){
			throw e;
		} finally{
			getDao().closeCurrentSession();
		}	
	}
	
	@Intercept
	public  String posttoGroups(String objectType,String pullType,String submitType, String objectIds, String userGroups,long userId,String filterUrl) throws Exception {
		
		try{
			long[] objects = Arrays.asList(objectIds.split(",")).stream().map(String::trim).mapToLong(Long::parseLong).toArray();
			
			//List<Long> list = Arrays.stream(objects).boxed().collect(Collectors.toList());
			
			System.out.println("abc :" + Arrays.asList(objectIds.split(",")));
			
			
			
			List<UserGroup> allowedUsrGrps =  userUserGroups(userId);
		    Set<UserGroup>  allowed = new HashSet<UserGroup>(allowedUsrGrps);
		    
			System.out.println("ObservationService class");
			
			for(long object : objects)
			{
					
					Class<?> clazz = Class.forName(objectType);
					DataObject _obj = (DataObject) clazz.newInstance();
					
					DataObject dataObj = _obj.get(object);
					
					Set<UserGroup> obvUsrGrps = dataObj.getUserGroups();
				
					Set<UserGroup> updatedObjUsrGrps = getDao().posttoGroups(objectType,dataObj,allowed,obvUsrGrps,pullType,submitType,userGroups,filterUrl);
					dataObj.setUserGroups(updatedObjUsrGrps);		
					dataObj.save();
			
			}			
			 return "success";
		} catch(Exception e) {
			throw e;
		} finally{
			
		}
		
	}   


	public Set<UserGroup> findAllByFilterRuleIsNotNull() throws Exception{
		
		try{
			List<UserGroup> userGroupsHavingFilterRule = getDao().findAllByFilterRuleIsNotNull(); 
			Set<UserGroup>  userGroupsWithFilterRule = new HashSet<UserGroup>(userGroupsHavingFilterRule);
			return userGroupsWithFilterRule;
		} catch(Exception e){
			throw e;
		} finally{
			
		}
		
	}

	public long[] findObjectIdsByFilterUrl(Map<String, String> filterUrlMap) {
		try{
			long[] objectIds = getDao().findObjectIdsByFilterRule(filterUrlMap);
			return objectIds;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	public UserGroup findByName(String name) {
		// TODO Auto-generated method stub
		name=name.trim();
		UserGroup userGroup=userGroupDao.findByName(name);
		return userGroup;
	}
}