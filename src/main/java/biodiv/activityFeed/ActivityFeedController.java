package biodiv.activityFeed;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import biodiv.Intercept;

@Path("/activityFeed")
public class ActivityFeedController {
	
	ActivityFeedService activityFeedService = new ActivityFeedService();

	@GET
	@Path("/feeds")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public Map<String,Object>   getFeeds(@QueryParam("rootHolderId") long rhId ,@QueryParam("rootHolderType") String rootHolderType,@QueryParam("activityHolderId") long ahId ,@QueryParam("activityHolderType") String activityHolderType,
			@QueryParam("activityType") String activityType,@QueryParam("feedType") String feedType ,@QueryParam("feedCategory") String feedCategory,@QueryParam("feedClass") String feedClass ,@QueryParam("feedPermission") String feedPermission,
			@QueryParam("feedOrder") String feedOrder ,@QueryParam("subRootHolderId") long srhId ,@QueryParam("subRootHolderType") String subRootHolderType ,@QueryParam("feedHomeObjectId") long fhoId,
			@QueryParam("feedHomeObjectType") String feedHomeObjectType ,@QueryParam("webaddress") String webaddress,@QueryParam("userGroupFromUserProfile") String ugfromUserProfile ,@QueryParam("refreshType") String refreshType,
			@QueryParam("timeLine") String timeLine ,@QueryParam("refTime") long refTym,@QueryParam("isShowable") boolean isShowable,@QueryParam("max") int max){
			
		
		System.out.println("inside controller");
		Map<String,Object>  af = activityFeedService.getFeeds( rhId, rootHolderType,activityType,feedType,feedCategory,feedClass,feedPermission,feedOrder,fhoId, feedHomeObjectType,
				 refreshType, timeLine, refTym,isShowable,max);
		return af;
	}
}
