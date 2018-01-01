package biodiv.activityFeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biodiv.Intercept;
import biodiv.comment.CommentService;
import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;
import biodiv.common.MyJson;
import biodiv.follow.FollowService;
import biodiv.user.User;

public class ActivityFeedService extends AbstractService<ActivityFeed>{
	
	private ActivityFeedDao activityFeedDao;
	
	FollowService followService =  new FollowService();
	CommentService commentService = new CommentService();
	
	public ActivityFeedService(){
		this.activityFeedDao = new ActivityFeedDao();
	}

	@Override
	public ActivityFeedDao getDao() {	
		return activityFeedDao;
	}
	
	
	public Map<String,Object>  getFeeds(long rhId,String rootHolderType,String activityType,String feedType,String feedCategory,String feedClass,String feedPermission,
			String feedOrder,long fhoId,String feedHomeObjectType,String refreshtype,String timeLine,long refTym ,boolean isShowable,int max){
		
		try{
			Date newerTimeRef = null;
			Date olderTimeRef = null;
			Map<String, Object> queryAndObject = ActivityFeed.getQuery(rhId,rootHolderType,activityType,feedType,feedCategory,feedClass,feedPermission,
					feedOrder,fhoId,feedHomeObjectType,refreshtype,timeLine,refTym ,isShowable,max,false,olderTimeRef);
			
			String hql = (String) queryAndObject.get("query");
			ActivityFeed _af = (ActivityFeed) queryAndObject.get("afObject");
			
			if(max>100){
				max=100;
			}
			
			List<Object[]> af = getDao().getFeeds(_af,hql,rhId,rootHolderType,feedType,feedPermission,feedOrder, fhoId, feedHomeObjectType,
					 refreshtype,timeLine, refTym ,max);
			
			if(feedOrder.equalsIgnoreCase("oldestFirst")){
				Collections.reverse(af);
			}
			 
			List<Map<String, Object>> feeds = new ArrayList<Map<String, Object>>();
			for(Object[] obj : af){
				String activityTyp = (String) obj[9];
				
				Map<String, Object> res = new HashMap<String, Object>();
				Map<String,Object> author = new HashMap<String,Object>();	
				author.put("id",(Long) obj[0]);
				author.put("name",(String)obj[1]);
				author.put("icon",(String) obj[2]); 
			    res.put("author", author);
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
			    if(activityTyp.equalsIgnoreCase("Added a comment")){
			    	MyJson myJson = new MyJson();
			    	myJson.setAid((Long) obj[3]);
			    	myJson.setName(null);
			    	myJson.setRo_id(null);
			    	myJson.setRo_type(null);
			    	String body = commentService.getCommentBody((Long) obj[5]);
			    	myJson.setDescription(body);
			    	myJson.setIs_migrated("true");
			    	myJson.setActivity_performed("Added a comment");
			    	myJson.setIs_scientific_name(null);
			    	res.put("descriptionJson", myJson);
				}else{
					res.put("descriptionJson", (MyJson) obj[17]);
				} 
				feeds.add(res);
			}
			Map<String,Object>  model = new HashMap<String, Object>();
			model.put("feeds", feeds);
			
			int feedSize = feeds.size();
			newerTimeRef = feedOrder.equalsIgnoreCase("latestFirst")? (Date)( feeds.get(0).get("lastUpdated")):(Date)(feeds.get(feedSize-1).get("lastUpdated"));
			olderTimeRef = feedOrder.equalsIgnoreCase("latestFirst")? (Date) (feeds.get(feedSize-1).get("lastUpdated")):(Date)(feeds.get(0).get("lastUpdated"));
			Date currentDate = new java.util.Date();

			
			queryAndObject = ActivityFeed.getQuery(rhId,rootHolderType,activityType,feedType,feedCategory,feedClass,feedPermission,
					feedOrder,fhoId,feedHomeObjectType,refreshtype,timeLine,refTym ,isShowable,max,true,olderTimeRef);
			ActivityFeed acf = (ActivityFeed) queryAndObject.get("afObject");
			hql = (String) queryAndObject.get("query");
			long count = getDao().getFeedCount(acf,hql);
			long remainingFeedCount = count ;
			
			
			Map<String,Object>  afResult = new HashMap<String, Object>();
		    afResult.put("model", model);
		    afResult.put("remainingFeedCount", remainingFeedCount);
		    afResult.put("olderTimeRef", olderTimeRef);
		    afResult.put("newerTimeRef", newerTimeRef);
		    afResult.put("currentTime", currentDate.getTime());
			return afResult;
		
		}catch(Exception e){
			throw e;
		}finally{
			
		}
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
