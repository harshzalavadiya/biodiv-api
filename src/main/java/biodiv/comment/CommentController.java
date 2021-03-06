package biodiv.comment;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import biodiv.observation.Observation;
import biodiv.observation.ObservationListService;
import biodiv.observation.ObservationService;
import net.minidev.json.JSONObject;

@Path("/comment")
public class CommentController {

	@Inject
	CommentService commentService;
	
	@Inject
	ObservationService observationService;
	
	@Inject
	ObservationListService observationListService;

	@POST
	@Path("/addComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	@Transactional
	public String addComment(@QueryParam("commentId") Long commentId, @QueryParam("commentBody") String commentBody,
			@QueryParam("tagUserId") String tagUserId, @QueryParam("commentHolderId") Long commentHolderId,
			@QueryParam("commentHolderType") String commentHolderType, @QueryParam("rootHolderId") Long rootHolderId,
			@QueryParam("rootHolderType") String rootHolderType, @QueryParam("mainParentId") Long mainParentId,
			@QueryParam("parentId") Long parentId, @QueryParam("subject") String subject,
			@QueryParam("commentType") String commentType, @QueryParam("newerTimeRef") Long newerTimeRef,
			@QueryParam("commentPostUrl") String commentPostUrl, @QueryParam("userLanguage") String userLang,
			@Context HttpServletRequest request) throws NumberFormatException, Exception {
		
		CommonProfile profile = AuthUtils.currentUser(request);
		String msg;
		if (commentBody != null && commentBody.trim().length() > 0) {
			msg = commentService.addComment(commentId, commentBody, tagUserId, commentHolderId, commentHolderType,
					rootHolderId, rootHolderType, mainParentId, parentId, subject, commentType, newerTimeRef,
					commentPostUrl, userLang, Long.parseLong(profile.getId()));
			
			//HACK HACK HACK for elastic
			//HACK HACK HACK for elastic
			//HACK HACK HACK for elastic
			Observation obv = observationService.findById(rootHolderId);
			Date lastrevised = new java.util.Date(newerTimeRef);;
			obv.setLastRevised(lastrevised);
			observationService.save(obv);
			JSONObject obj = new JSONObject();
			SimpleDateFormat out = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
			SimpleDateFormat in = new SimpleDateFormat("EEE MMM dd YYYY HH:mm:ss");
			String newDate=out.format(obv.getLastRevised());
			obj.put("lastrevised",newDate);
			observationListService.update("observation", "observation", obv.getId().toString(), obj.toString());
			//HACK HACK HACK for elastic
			//HACK HACK HACK for elastic
			//HACK HACK HACK for elastic

		} else {
			msg = "error in comment body";
		}
		return msg;

	}

	@POST
	@Path("/removeComment")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	@Transactional
	public String removeComment(@QueryParam("commentId") Long commentId, @Context HttpServletRequest request) {
		String msg;
		CommonProfile profile = AuthUtils.currentUser(request);
		if (commentService.removeComment(commentId, Long.parseLong(profile.getId()))) {
			msg = "success";
		} else {
			msg = "failed";
		}
		return msg;

	}
}
