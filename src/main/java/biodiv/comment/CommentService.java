package biodiv.comment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.HtmlEmail;

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
import biodiv.userGroup.UserGroupMailingService;
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
	private CommentMailingService commentMailingService;
	
	@Inject
	Configuration config;
	
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
			String userLang,long userId) throws Exception {
		
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
				//for updating reco comment
				//when cnName situation remains as it is
				//	c = commentDao.findByAuthorCommentHolderAndRootHolder(lang,user,commentBody,commentHolderType,commentHolderId,rootHolderType,rootHolderId);
				//for updating reco comment
				//if( (c !=null && !commentHolderType.equals("species.participation.Recommendation")) || c==null )	{
					if(parentId != null){
						c = new Comment(lang,user,commentBody.trim(),commentHolderId,commentHolderType,dateCreated,lastUpdated,
								rootHolderId,rootHolderType,mainParentId,parentId,subject);
					}else{
						c = new Comment(lang,user,commentBody.trim(),commentHolderId,commentHolderType,dateCreated,lastUpdated,rootHolderId,
								 rootHolderType);
					}
				//}
				
			 
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
					 userTagNotify(rootHolderType,rootHolderId,tagUserIds,user,commentBody);
				 }
			 }
			 List<User> allFollowersOfTheObject = findFollowersOfObject(rootHolderId,"species.participation.Observation");
			 addToCommentMail(allFollowersOfTheObject,user,commentBody,rootHolderId);
			
			return "success";
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	private List<User> findFollowersOfObject(Long objectId, String objectToFollowType) {
		try{
			List<User> followers = followService.findAllFollowersOfObject(objectId,objectToFollowType);
			return followers;
		}catch(Exception e){
			throw e;
		}
	}

	

	private Long fetchMainThreadId(Comment comment) {
		return comment.getMainParentId();
	}

	private void userTagNotify(String objectType,Long objectToFollowId,long[] tagUserIds,User whoTaggedThem,String comment) throws Exception {
		
		for(long tagUserId : tagUserIds){
			User taggedUser = userService.findById(tagUserId);
			Observation obv = observationService.findById(objectToFollowId);
			addToTagMail(whoTaggedThem,taggedUser,comment,obv);
			followService.addFollower(null,objectType,objectToFollowId,taggedUser);
		}
		
		
	}
	private void addToCommentMail(List<User> allFollowersOfTheObject, User user, String commentBody, Long rootHolderId) throws Exception {
		Observation obv = observationService.findById(rootHolderId);
		List<User> allBccs = commentMailingService.getAllBccPeople();
		for(User bcc : allBccs){
			HtmlEmail emailToBcc = commentMailingService.buildCommentPostMailMessage(bcc.getEmail(),
					bcc,user,obv,commentBody);
		}
		if(user.getSendNotification()){
			HtmlEmail emailToPostingUser = commentMailingService.buildCommentPostMailMessage(user.getEmail(),
					user,user,obv,commentBody);
		}
		if(config.getString("mail.sendToFollowers").equalsIgnoreCase("true")){
			
			//System.out.println("send to followers");
			for(User follower : allFollowersOfTheObject){
				if(!commentMailingService.isTheFollowerInBccList(follower.getEmail())){
					if(follower.getSendNotification()){
						HtmlEmail emailToFollowers = commentMailingService.buildCommentPostMailMessage(follower.getEmail(),
								follower,user,obv,commentBody);
					}
				}	
			}
		}
		
			
		if(!commentMailingService.isAnyThreadActive()){
			System.out.println("no thread is active currently");
			Thread th = new Thread(commentMailingService);
			th.start();
		}
			
		
	}
	
	private void addToTagMail(User whoTaggedThem, User taggedUser, String comment,Observation obv) throws Exception {
		List<User> allBccs = commentMailingService.getAllBccPeople();
		for(User bcc : allBccs){
			HtmlEmail emailToBcc = commentMailingService.buildTaggingMailMessage(bcc.getEmail(),
					taggedUser,whoTaggedThem,obv,comment);
		}
		if(taggedUser.getSendNotification()){
			HtmlEmail emailToTagged = commentMailingService.buildTaggingMailMessage(taggedUser.getEmail(),
					taggedUser,whoTaggedThem,obv,comment);
		}
		if(!commentMailingService.isAnyThreadActive()){
			System.out.println("no thread is active currently");
			Thread th = new Thread(commentMailingService);
			th.start();
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
