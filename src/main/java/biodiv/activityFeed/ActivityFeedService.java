package biodiv.activityFeed;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biodiv.Intercept;
import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;
import biodiv.follow.FollowService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupDao;

public class ActivityFeedService extends AbstractService<ActivityFeed>{
	
	private ActivityFeedDao activityFeedDao;
	
	FollowService followService =  new FollowService();
	
	public ActivityFeedService(){
		this.activityFeedDao = new ActivityFeedDao();
	}

	@Override
	public ActivityFeedDao getDao() {	
		return activityFeedDao;
	}
	
	
	public List<List<Map<String, Object>>> getFeeds(long rhId,String rootHolderType,String activityType,String feedType,String feedCategory,String feedClass,String feedPermission,
			String feedOrder,long fhoId,String feedHomeObjectType,String refreshtype,String timeLine,long refTym ,boolean isShowable,int max){
		
		System.out.println("inside service");
		
		ActivityFeed _af = new ActivityFeed();
		
		String selectClause = "select usr.id,usr.name,usr.icon,af.id,af.version,af.activityHolderId,af.activityHolderType"
				+ ",af.activityDescrption,af.activityRootType,af.activityType,af.dateCreated,af.lastUpdated,af.rootHolderId"
				+ ",af.rootHolderType,af.subRootHolderId,af.subRootHolderType,af.isShowable";	
		String fromClause = "from ActivityFeed af inner join User usr on af.user.id = usr.id";	
		String whereClause = "where";	
		String orderClause = "order by af.lastUpdated desc";
		
		if(isShowable){
			if(whereClause == "where"){
				whereClause += " af.isShowable =:isShowable";
			}else{
				whereClause += " and af.isShowable =:isShowable";
			}	
			_af.setIsShowable(isShowable);
		}
		
		if(feedCategory !=null && (feedCategory != "all")){
			if(whereClause == "where"){
				whereClause += " af.rootHolderType =:rootHolderType";
			}else{
				whereClause += " and af.rootHolderType =:rootHolderType";
			}
			_af.setRootHolderType(rootHolderType);
		}
		
		if(feedClass !=null){
			if(whereClause == "where"){
				whereClause += " af.activityType = :activitytype";
			}else{
				whereClause += " and af.activityType = :activitytype";
			}
			_af.setActivityType(activityType);
		}
		
		if(timeLine.equalsIgnoreCase("older")){
			
			if(whereClause == "where"){
				whereClause += " af.lastUpdated < :lastUpdated";
			}else{
				whereClause += " and af.lastUpdated < :lastUpdated";
			}
			Timestamp refTime = new java.sql.Timestamp(refTym);
			_af.setLastUpdated(refTime);
		}else{
			System.out.println("olderrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
			if(whereClause == "where"){
				whereClause += " af.lastUpdated > :lastUpdated";
			}else{
				whereClause += " and af.lastUpdated > :lastUpdated";
			}
			Timestamp refTime = new java.sql.Timestamp(refTym);
			_af.setLastUpdated(refTime);
		}
		
		switch(feedType){
		
		case "generic":
			if(whereClause == "where"){
				whereClause += " af.rootHolderType = :rootHolderType";
			}else{
				whereClause += " and af.rootHolderType = :rootHolderType";
			}
			_af.setRootHolderType(rootHolderType);
			break;
			
		case "specific":
			if(whereClause == "where"){
				whereClause += " af.rootHolderType = :rootHolderType";
				whereClause += " and af.rootHolderId = :rootHolderId";
			}else{
				whereClause += " and af.rootHolderType = :rootHolderType";
				whereClause += " and af.rootHolderId = :rootHolderId";
			}		
			_af.setRootHolderType(rootHolderType);
			_af.setRootHolderId(rhId);
			break;
			
		case "user":
		case "groupSpecific":
		case "myFeeds":
			if(feedType == "user"){
				
			}
			break;
			
		default:
			break;
			
		}
		
		
		String hql =selectClause+" "+fromClause+" " + whereClause+" "+ orderClause;	
		List<Object[]> af = getDao().getFeeds(_af,hql,rhId,rootHolderType,feedType,feedPermission,feedOrder, fhoId, feedHomeObjectType,
				 refreshtype,timeLine, refTym ,max);
		
		if(feedOrder.equalsIgnoreCase("oldestFirst")){
			Collections.reverse(af);
		}
		 
		List<List<Map<String, Object>>> results = new ArrayList<List<Map<String, Object>>>();
		List<Map<String, Object>> feeds = new ArrayList<Map<String, Object>>();
		
		for(Object[] obj : af){
			Map<String, Object> res = new HashMap<String, Object>();
			Map<String,Object> user = new HashMap<String,Object>();	
			user.put("id",(Long) obj[0]);
			user.put("name",(String)obj[1]);
			user.put("icon",(String) obj[2]); 
		    res.put("user", user);
		    res.put("id", (Long) obj[3]);
		    res.put("version", (Long) obj[4]);
		    res.put("activityHolderId", (Long) obj[5]);
		    res.put("activityHolderType", (String) obj[6]);
		    res.put("activityDescription", (String) obj[7]);
		    res.put("activityRootType", (String) obj[8]);
		    res.put("activityType", (String) obj[9]);
		    res.put("dateCreated", (Date) obj[10]);
		    res.put("lastUpdated", (Date) obj[11]);
		    res.put("rootHolderId", (Long) obj[12]);
		    res.put("rootHolderType", (String) obj[13]);
		    res.put("subRootHolderId", (Long) obj[14]);
		    res.put("subRootHolderType", (String) obj[15]);
		    res.put("isShowable", (boolean) obj[16]);
			feeds.add(res);
		}
		
		
		selectClause = "select count(*)";
		hql = selectClause+" "+fromClause+" " + whereClause;
		long count = getDao().getFeedCount(_af,hql);
		long countMinusMax = count-max<0?0:(count-max);
		count = max==0?0:countMinusMax;
		
		List<Map<String, Object>> meta = new ArrayList<Map<String, Object>>();
		Map<String,Object> extra = new HashMap<String, Object>();
	    extra.put("remainingFeedCount", (Long) count);
	    meta.add(extra);
	    
	    results.add(feeds);
		results.add(meta);
		return results;
	}
	
	@Intercept
	public void addActivityFeed(User user,Map<String, Object> afNew,Object objectToFollow){
		
		try{			
				ActivityFeed af = new ActivityFeed(user,(String)afNew.get("activityDescrption"),(Long)afNew.get("activityHolderId"),
						(String)afNew.get("activityHolderType"),(String)afNew.get("activityType") ,(Date)afNew.get("dateCreated"),
						(Date)afNew.get("lastUpdated"),(Long)afNew.get("rootHolderId"), (String)afNew.get("rootHolderType"),
						(Long)afNew.get("subRootHolderId"),(String)afNew.get("subRootHolderType"),(Boolean)afNew.get("isShowable"));
				
				save(af);		
			
				//Follow
				followService.addFollower(objectToFollow,(Long)afNew.get("rootHolderId"),user);
				
		}catch(Exception e){
			throw e;
		}finally{
			
		}
			
	}

}
