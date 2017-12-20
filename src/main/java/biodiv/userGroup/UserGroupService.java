package biodiv.userGroup;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.jvnet.hk2.annotations.Service;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

import biodiv.Intercept;
import biodiv.activityFeed.ActivityFeed;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.common.AbstractService;
import biodiv.common.DataObject;
import biodiv.observation.Observation;
import biodiv.observation.ObservationService;
import biodiv.user.User;
import biodiv.user.UserService;
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
		
//		System.out.println("beforeeeeeeeeeeeeeeeeeeeeee :"+ System.identityHashCode(HibernateUtil.getSessionFactory().getCurrentSession()));
//		StatelessSession session = null;
//		Transaction tx = null;
		ActivityFeedService activityFeedService =  new ActivityFeedService();
		try{
//			 session = HibernateUtil.getSessionFactory().openStatelessSession();
//			 tx = session.beginTransaction();
//			 System.out.println("afterrrrrrrrrrrrrrrr1 :"+ System.identityHashCode(session));
//			 System.out.println("afterrrrrrrrrrrrrrrr2 :"+ System.identityHashCode(HibernateUtil.getSessionFactory().getCurrentSession()));
			long[] objects = Arrays.asList(objectIds.split(",")).stream().map(String::trim).mapToLong(Long::parseLong).toArray();
			
			//List<Long> list = Arrays.stream(objects).boxed().collect(Collectors.toList());
			
			//System.out.println("abc :" + Arrays.asList(objectIds.split(",")));
			
//			List<List<String>> l = UserGroup.collate(Arrays.asList(objectIds.split(",")), 3, 3);
//			System.out.println(l);
//			System.out.println(l.get(1));
//			List<String> s = l.get(1);
//			System.out.println(s);
//			System.out.println(s.stream().map(String::trim).mapToLong(Long::parseLong).toArray());
//			long[] abc = s.stream().map(String::trim).mapToLong(Long::parseLong).toArray();
//			System.out.println(abc[0]);
			
			
			//System.out.println(UserGroup.splitBytes(objects,3));
			
			UserService userService = new UserService();
			User user = userService.findById((Long)userId);
			User admin = userService.findById((long)1);
			List<UserGroup> allowedUsrGrps =  userUserGroups(userId);
		    Set<UserGroup>  allowed = new HashSet<UserGroup>(allowedUsrGrps);
		    Set<UserGroup> userGroupsWithFilterRule = findAllByFilterRuleIsNotNull();
			System.out.println("ObservationService class");
			
			long i = 1;

			Map<Long, Long> groupFeed_ByUser = new HashMap<Long,Long>();
			Map<Long, Long> groupFeed_ByAdmin = new HashMap<Long,Long>();
			DataObject typeOfObject = null;
			for(long object : objects)
			{
					
					Class<?> clazz = Class.forName(objectType);
					DataObject _obj = (DataObject) clazz.newInstance();
					
					DataObject dataObj = _obj.get(object);
					typeOfObject = dataObj;
					Set<UserGroup> obvUsrGrps = dataObj.getUserGroups();
					
					Set<UserGroup> userGroupsContainingObv = UserGroup.findAllContainingObj(objectType,(Object)dataObj,userGroupsWithFilterRule);
					
					Set<UserGroup> updatedObjUsrGrps = getDao().posttoGroups(objectType,dataObj,allowed,userGroupsContainingObv,obvUsrGrps,pullType,submitType,userGroups,filterUrl);
					
					dataObj.setUserGroups(updatedObjUsrGrps);		
					dataObj.save();
					
					//activityFeed addition starts here for Object Entry
					
					String activityDescription = UserGroup.getActivityObjectType((Object)dataObj,submitType,"Object",(long)0);
					Set<UserGroup> newAddedOrRemovedUsrGrps = new HashSet<>(updatedObjUsrGrps);
					
					if(submitType.equalsIgnoreCase("post")){
						newAddedOrRemovedUsrGrps.removeAll(obvUsrGrps);
						Set<UserGroup> newAddedUsrGrpsByAdmin = new HashSet<>(newAddedOrRemovedUsrGrps);
						newAddedUsrGrpsByAdmin.retainAll(userGroupsContainingObv);
						Set<UserGroup> newAddedUsrGrpsByUsr = new HashSet<>(newAddedOrRemovedUsrGrps);
						newAddedUsrGrpsByUsr.removeAll(newAddedUsrGrpsByAdmin);
						
						for(UserGroup ug : newAddedUsrGrpsByUsr){
							 long ugId = ug.getId();
							 Map<String, Object> afNew = ActivityFeed.createMapforAf("Object",object,dataObj,"species.groups.UserGroup",ugId,"Posted resource",activityDescription,true);	 
							 activityFeedService.addActivityFeed(user,afNew,dataObj);
							 if(pullType.equalsIgnoreCase("bulk")){
								 groupFeed_ByUser.merge(ugId, (long) 1, Long::sum);
							 }
							 
						}
						
						for(UserGroup ug : newAddedUsrGrpsByAdmin){						
							long ugId = ug.getId();
							Map<String, Object> afNew = ActivityFeed.createMapforAf("Object",object,dataObj,"species.groups.UserGroup",ugId,"Posted resource",activityDescription,true);	 
							activityFeedService.addActivityFeed(admin,afNew,dataObj);
							if(pullType.equalsIgnoreCase("bulk")){
								groupFeed_ByAdmin.merge(ugId, (long) 1, Long::sum);
							}
						}
					}else{
						Set<UserGroup> previousUsrGrps = new HashSet<>(obvUsrGrps);
						previousUsrGrps.removeAll(newAddedOrRemovedUsrGrps);
						for(UserGroup ug : previousUsrGrps){
							long ugId = ug.getId();
							Map<String, Object> afNew = ActivityFeed.createMapforAf("Object",object,dataObj,"species.groups.UserGroup",ugId,"Removed resoruce",activityDescription,true);
							activityFeedService.addActivityFeed(user,afNew,dataObj);
							if(pullType.equalsIgnoreCase("bulk")){
								groupFeed_ByUser.merge(ugId, (long) 1, Long::sum);
							}
						}
					}
					//activityFeed addition ends here for Object Entry
						
					if(i%50 == 0){
						HibernateUtil.getSessionFactory().getCurrentSession().flush();
						HibernateUtil.getSessionFactory().getCurrentSession().clear();
					}
					
					i++;
			}	
			
			//activityFeed for UserGroup entry starts here
			if(pullType.equalsIgnoreCase("bulk")){
				if(submitType.equalsIgnoreCase("post")){
					for(Long ugId : groupFeed_ByUser.keySet()){
						UserGroup ug = findById(ugId);
						long countOfObjs = groupFeed_ByUser.get(ugId);
						String description = UserGroup.getActivityObjectType((Object)typeOfObject,submitType,"UserGroup",countOfObjs);
						Map<String, Object> afNew = ActivityFeed.createMapforAf("UserGroup",ugId,typeOfObject,"species.groups.UserGroup",ugId,"Posted resource",description,true);
						activityFeedService.addActivityFeed(user, afNew, ug);
					}
					
					for(Long ugId : groupFeed_ByAdmin.keySet()){
						UserGroup ug = findById(ugId);
						long countOfObjs = groupFeed_ByAdmin.get(ugId);
						String description = UserGroup.getActivityObjectType((Object)typeOfObject,submitType,"UserGroup",countOfObjs);
						Map<String, Object> afNew = ActivityFeed.createMapforAf("UserGroup",ugId,typeOfObject,"species.groups.UserGroup",ugId,"Posted resource",description,true);
						activityFeedService.addActivityFeed(admin, afNew, ug);
					}
				}else{
					for(Long ugId : groupFeed_ByUser.keySet()){
						UserGroup ug = findById(ugId);
						long countOfObjs = groupFeed_ByUser.get(ugId);
						String description = UserGroup.getActivityObjectType((Object)typeOfObject,submitType,"UserGroup",countOfObjs);
						Map<String, Object> afNew = ActivityFeed.createMapforAf("UserGroup",ugId,typeOfObject,"species.groups.UserGroup",ugId,"Removed resoruce",description,true);
						activityFeedService.addActivityFeed(user, afNew, ug);
					}
					
				}
			}
			//activityFeed for UserGroup entry ends here
			
			//Follwer code
			
			
			
			
			 return "success";
		} catch(Exception e) {
			throw e;
		} finally{
//			tx.commit();
//			session.close();
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
}