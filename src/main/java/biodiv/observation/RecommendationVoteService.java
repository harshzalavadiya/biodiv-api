package biodiv.observation;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.activityFeed.ActivityFeedService;
import biodiv.comment.CommentService;
import biodiv.common.AbstractService;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.user.User;
import biodiv.user.UserService;

public class RecommendationVoteService extends AbstractService<RecommendationVote> {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private RecommendationVoteDao recommendationVoteDao;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ActivityFeedService activityFeedService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	RecommendationVoteService(RecommendationVoteDao recommendationVoteDao){
		super(recommendationVoteDao);
		this.recommendationVoteDao = recommendationVoteDao;
	}

	public RecommendationVote findByAuthorAndObservation(User author, Observation obv) {
		
		try{
			RecommendationVote rv = recommendationVoteDao.findByAuthorAndObservation(author,obv);
			return rv;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

	public void removeFromRecommendationVote(RecommendationVote existingRecVote) {
		
		
	}

	public void updateSpeciesTimeStamp(RecommendationVote recommendationVoteInstance) {
		Recommendation r = recommendationVoteInstance.getRecommendationByRecommendationId();
		Taxon tax = null;
		if(r !=null){
			tax = r.getTaxonConcept();
		}
		//Species sp=null;
		if(tax !=null){
			 //sp = speciesService.findById(tax.getSpeciesId());
		}
		//if(sp !=null){
			//sp.setLastUpdated(new Date());
			//speciesService.save(sp);
		//}
		
	}

	public void addRecoComment(Long commentId,String commentBody, String tagUserId, Long commentHolderId, String commentHolderType,
			Long rootHolderId, String rootHolderType,Long mainParentId,Long parentId,String subject, String commentType, Long newerTimeRef, String commentPostUrl,
			String userLang,long userId) throws Exception {
		
		if (commentBody != null && commentBody.trim().length() > 0) {
			        commentService.addComment(commentId, commentBody, tagUserId, commentHolderId, commentHolderType,
					rootHolderId, rootHolderType, mainParentId, parentId, subject, commentType, newerTimeRef,
					commentPostUrl, userLang,userId);
		}
		
	}
}