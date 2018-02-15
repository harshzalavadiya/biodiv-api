package biodiv.activityFeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.comment.Comment;
import biodiv.comment.CommentService;
import biodiv.common.AbstractService;
import biodiv.common.MyJson;
import biodiv.follow.FollowService;
import biodiv.user.User;

public class ActivityFeedService extends AbstractService<ActivityFeed>{
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private ActivityFeedDao activityFeedDao;
	
	@Inject
	private FollowService followService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	ActivityFeedService(ActivityFeedDao activityFeedDao){
		super(activityFeedDao);
		this.activityFeedDao = activityFeedDao;
		log.trace("ActivityFeedService constructor");
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
			
			List<Object[]> af = activityFeedDao.getFeeds(_af,hql,rhId,rootHolderType,feedType,feedPermission,feedOrder, fhoId, feedHomeObjectType,
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
			    	
			    	Comment comment = commentService.findById((Long) res.get("activityHolderId"));
			    	Long parentId = comment.getParentId();
			    	if(parentId != null){
			    		Comment parentComment = commentService.findById(parentId);
			    		User parentCommentAuthor = parentComment.getUser();
			    		myJson.setName(parentCommentAuthor.getName());
			    		myJson.setRo_id(parentCommentAuthor.getId());
			    		myJson.setRo_type("user");
			    		myJson.setActivity_performed("In reply to");
			    	}else{
			    		myJson.setName(null);
				    	myJson.setRo_id(null);
				    	myJson.setRo_type(null);
				    	myJson.setActivity_performed("Added a comment");
			    	}
			    	
			    	String body = commentService.getCommentBody((Long) obj[5]);
			    	myJson.setDescription(body);
			    	myJson.setIs_migrated("true");
			    	
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
			long count = activityFeedDao.getFeedCount(acf,hql);
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
	
	@Transactional
	public void addActivityFeed(User user,Map<String, Object> afNew,Object objectToFollow,String objectToFollowType){
		
		try{			
				ActivityFeed af = new ActivityFeed(user,(String)afNew.get("activityDescrption"),(Long)afNew.get("activityHolderId"),
						(String)afNew.get("activityHolderType"),(String)afNew.get("activityType") ,(Date)afNew.get("dateCreated"),
						(Date)afNew.get("lastUpdated"),(Long)afNew.get("rootHolderId"), (String)afNew.get("rootHolderType"),
						(Long)afNew.get("subRootHolderId"),(String)afNew.get("subRootHolderType"),(MyJson) afNew.get("descriptionJson"),
						(Boolean)afNew.get("isShowable"));
				
				save(af);		
			
				//Follow
				if(objectToFollow != null || objectToFollowType != null){
					String objectTyp = objectToFollowType;
					followService.addFollower(objectToFollow,objectTyp,(Long)afNew.get("rootHolderId"),user);
				}
				
				
		}catch(Exception e){
			throw e;
		}finally{
			
		}
			
	}
	
	public  Map<String, Object> createMapforAf(String rootType,Long rhId,Object rootHolder,String rootHolderTyp,String activityHolderType,Long activityHolderId,
			String activityType,String activityPerformed,String activityDescrption,String description ,String name,String ro_type,Boolean isShowable,Long subRootId) {
		
		//String activityDescrption;
	    //Long activityHolderId ;  
		//String activityHolderType ;
		//String activityType ;
		Date dateCreated = new java.util.Date();
		Date lastUpdated = new java.util.Date();
		
		

		//Boolean isShowable;
		
		 //activityDescrption = "Posted observation to group";
	     //activityHolderId = ahId;  
		 //activityHolderType = "species.groups.UserGroup";
		 //activityType = "Posted Resource";
		 Long rootHolderId ;
		 String rootHolderType ;
		 Long subRootHolderId ;
		 String subRootHolderType;
		 
		    MyJson myJson = new MyJson();
	    	//myJson.setAid((Long) obj[3]);
		    
		    //need to check on next activity written in backend
		    if(!activityType.equalsIgnoreCase("Added a comment")){
		    	if(ro_type != null){
			    	myJson.setName(name);
			    	myJson.setRo_id(activityHolderId);
			    	myJson.setRo_type(ro_type);
			    }
		    	
		    	if(description !=null){
		    		myJson.setDescription(description);
		    	}
		    	myJson.setIs_migrated("true");
		    	
		    	myJson.setActivity_performed(activityPerformed);
		    	//myJson.setIs_scientific_name(null);
		    }
		    
	    	
		if(rootType.equalsIgnoreCase("UserGroup")){
			rootHolderId = rhId;
			rootHolderType = "species.groups.UserGroup";
			subRootHolderId = rhId;
			subRootHolderType = "species.groups.UserGroup";
		}else{
			
			rootHolderId = rhId;
			subRootHolderId = rhId;
			if(rootHolderTyp == null){
				rootHolderType = ActivityFeed.getType(rootHolder);
			}else{
				rootHolderType = rootHolderTyp;
			}
			 
			if(rootHolderTyp == null){
				subRootHolderType = ActivityFeed.getType(rootHolder);
			}else{
				subRootHolderType = rootHolderTyp;
			}
			 isShowable = true;
			 
			 if(activityHolderType.equalsIgnoreCase("species.participation.Comment")){	
				 
				 subRootHolderType = activityHolderType;
				 subRootHolderId = subRootId;
			 }
		}
		 
		 
		  
		 Map<String,Object> afNew =  new HashMap<String, Object>();
		 afNew.put("activityDescrption",activityDescrption);
		 afNew.put("activityHolderId",activityHolderId);
		 afNew.put("activityHolderType",activityHolderType);
		 afNew.put("activityType",activityType);
		 afNew.put("dateCreated",dateCreated);
		 afNew.put("lastUpdated",lastUpdated);
		 afNew.put("rootHolderId",rootHolderId);
		 afNew.put("rootHolderType",rootHolderType);
		 afNew.put("subRootHolderId",subRootHolderId);
		 afNew.put("subRootHolderType",subRootHolderType);
		 afNew.put("isShowable",isShowable);
		 afNew.put("descriptionJson", myJson);
		 
		return afNew;
	}

	private boolean isMainThread(Comment comment) {
		return comment.getParentId() ==  null;
	}

	public void deleteActivityFeed(Long objectId, String activityType, String position) {
		
		String query = "";
		Map<String,Object> params =  new HashMap<String, Object>();
		params.put("type", activityType);
		params.put("id", objectId);
		switch(position){
		
			case "activityHolder":
				query = "from ActivityFeed where activityType =:activityType"+" and activityHolderId =:objectId";
				
				break;
			case "rootHolder":
				query = "from ActivityFeed where activityType =:activityType"+" and rootHolderId =:objectId";
				break;
			default:
				//
		}
		ActivityFeed af = activityFeedDao.findActivityFeed(query,params);
		delete(af.getId());
	}

}
