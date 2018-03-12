package biodiv.comment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import biodiv.Transactional;
import biodiv.activityFeed.ActivityFeed;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.follow.FollowService;
import biodiv.observation.Observation;
import biodiv.observation.ObservationListService;
import biodiv.observation.ObservationService;
import biodiv.user.User;
import biodiv.user.UserService;
import net.minidev.json.JSONObject;

public class CommentService extends AbstractService<Comment>{

	
	private CommentDao commentDao;
	@Inject
	private LanguageService languageService;
	@Inject
	private UserService userService;
	@Inject
	private ActivityFeedService activityFeedService ;
	@Inject
	private FollowService followService;
	@Inject
	private ObservationService observationService;
	@Inject
	private ObservationListService observationListService;
	
	@Inject
	CommentService(CommentDao commentDao){
		super(commentDao);
		this.commentDao = commentDao;
	}
	
	@Transactional
	public String getCommentBody(long id){
		
		String body;
		
		try{
			body = commentDao.getCommentBody(id);
			return body;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		
	}

	@Transactional
	public String addComment(Long commentId,String commentBody, String tagUserId, Long commentHolderId, String commentHolderType,
			Long rootHolderId, String rootHolderType,Long mainParentId,Long parentId,String subject, String commentType, Long newerTimeRef, String commentPostUrl,
			String userLang,long userId) {
		
		try{
			Language lang = languageService.findByTwoLetterCode(userLang);
			User user = userService.findById((Long)userId);
			Date dateCreated = new java.util.Date(newerTimeRef);
			Date lastUpdated = new java.util.Date(newerTimeRef);
			//Timestamp refTym = new java.sql.Timestamp(newerTimeRef);
			
			if(parentId != null){
				System.out.println("**************************************************************************");
				System.out.println("insdie parenId not null");
				Comment parentComment = findById(parentId);
				mainParentId = (parentComment.getMainParentId() == null)?parentComment.getId():null;
				commentHolderId = parentComment.getId();
				commentHolderType = Comment.getClassType(parentComment);
				rootHolderId = parentComment.getRootHolderId();
				rootHolderType = parentComment.getRootHolderType();
			}
			
			long[] tagUserIds = null;
			System.out.println("taguserIds "+tagUserIds);
			if(tagUserId != null){
				tagUserIds =  Arrays.asList(tagUserId.split(",")).stream().map(String::trim).mapToLong(Long::parseLong).toArray();
			}
			Comment c = null;
			if(commentId == null){
				if(parentId != null){
					c = new Comment(lang,user,commentBody.trim(),commentHolderId,commentHolderType,dateCreated,lastUpdated,
							rootHolderId,rootHolderType,mainParentId,parentId,subject);
				}else{
					c = new Comment(lang,user,commentBody.trim(),commentHolderId,commentHolderType,dateCreated,lastUpdated,rootHolderId,
							 rootHolderType);
				}
			 
			}else{
				c = findById(commentId);
				c.setBody(commentBody.trim());
				c.setLanguage(lang);
				c.setLastUpdated(lastUpdated);
			}
			
			 save(c);
			
			//activityFeed
			 if(commentId == null){
				
					 	String activityDescription = "";
						Long activityHolderId = c.getId();
						System.out.println("comment id after save "+activityHolderId);
						Long subRootId = isMainThread(c)?activityHolderId:fetchMainThreadId(c);
						Map<String, Object> afNew = activityFeedService.createMapforAf("Object",rootHolderId,null,
									rootHolderType,"species.participation.Comment",activityHolderId,
									"Added a comment","Added a comment",activityDescription,null,null,null,null,true,subRootId,dateCreated,lastUpdated);
							
						activityFeedService.addActivityFeed(user, afNew, null,(String)afNew.get("rootHolderType"));
	 
			 }
			// activityFeed end
			 if(tagUserIds != null){
				 if(tagUserIds.length > 0){
					 userTagNotify(rootHolderType,rootHolderId,tagUserIds);
				 }
			 }
			 
			
			return "success";
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	private Long fetchMainThreadId(Comment comment) {
		return comment.getMainParentId();
	}

	private void userTagNotify(String objectType,Long objectToFollowId,long[] tagUserIds) {
		
		for(long tagUserId : tagUserIds){
			User taggedUser = userService.findById(tagUserId);
			followService.addFollower(null,objectType,objectToFollowId,taggedUser);
		}
		
		
	}

	public boolean removeComment(Long commentId, long userId) {
		
		try{
			Comment comment = findById(commentId);
			Observation obv = observationService.findById(comment.getRootHolderId());
			Date lastRevised = new Date();
			if(comment != null){
				User author = comment.getUser();
				if(author.getId() == userId){
					
					delete(commentId);
					activityFeedService.deleteActivityFeed(commentId,"Added a comment","activityHolder");
					if(isMainThread(comment)){
						deleteChildren(commentId);
					}
					obv.setLastRevised(lastRevised);
					observationService.save(obv);
					JSONObject obj = new JSONObject();
					SimpleDateFormat out = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
					SimpleDateFormat in = new SimpleDateFormat("EEE MMM dd YYYY HH:mm:ss");
					String newDate=out.format(obv.getLastRevised());
					obj.put("lastrevised",newDate);
					observationListService.update("observation", "observation", obv.getId().toString(), obj.toString());
					return true;
				}
				System.out.println("not your comment ****************************");
				return false;
			}
			return false;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	private void deleteChildren(Long commentId) {
		try{
			List<Comment> childComments = findAllByParentId(commentId);
			for(Comment c : childComments){
				//c.setParentId(null);
				//save(c);
				Long childId = c.getId();
				delete(childId);
				activityFeedService.deleteActivityFeed(childId,"Added a comment","activityHolder");
			}
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	private List<Comment> findAllByParentId(Long commentId) {
		
		try{
			List<Comment> childComments = commentDao.findAllByParentId(commentId);
			return childComments;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	private boolean isMainThread(Comment comment) {
		return comment.getParentId() ==  null;
	}

	public Long getTotalRecoCommentCount(Long recoId, Long obvId) {
		try{
			Long totalCount = commentDao.getTotalRecoCommentCount(recoId,obvId);
			return totalCount;
		}catch(Exception e){
			throw e;
		}finally{
			//
		}
	}
}
