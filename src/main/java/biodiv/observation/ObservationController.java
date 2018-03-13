package biodiv.observation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import biodiv.Transactional;
import biodiv.auth.AuthUtils;
import biodiv.common.DataObject;
import biodiv.userGroup.UserGroup;

@Path("/observation")
public class ObservationController {

	@Inject
	ObservationService observationService;

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Observation show(@PathParam("id") long id) {

		try {
			Observation observation = observationService.show(id);
			return observation;
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}

	}

	@GET
	@Path("/{id}/userGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroup> obvUserGroups(@PathParam("id") long id) {
		List<UserGroup> usrGrps = observationService.obvUserGroups(id);
		return usrGrps;
	}

	@GET
	@Path("/customFields")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getCustomFields(@QueryParam("obvId") Long obvId) {

		List<Map<String, Object>> cf = observationService.getCustomFields(obvId);
		return cf;
	}

	@POST
	@Path("/updateCustomField")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	public String updateCustomField(@QueryParam("fieldValue") String fieldValue, @QueryParam("cfId") Long cfId,
			@QueryParam("obvId") Long obvId, @Context HttpServletRequest request) throws NumberFormatException, Exception {

		CommonProfile profile = AuthUtils.currentUser(request);
		String msg = observationService.updateInlineCf(fieldValue, cfId, obvId, Long.parseLong(profile.getId()));
		return msg;
	}

	@GET
	@Path("/resource/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ObservationResource> getResource(@PathParam("id") long id) {
		List<ObservationResource> observationResources = observationService.getResouce(id);
		return observationResources;
	}

	@POST
	@Path("/updategroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	public Object updateGroup(@QueryParam("objectid") Long objectid, @QueryParam("newGroupId") Long newGroupId,
			@QueryParam("oldGroupId") Long oldGroupId, @Context HttpServletRequest request) {
		CommonProfile profile = AuthUtils.currentUser(request);
		Object observation = observationService.updateGroup(objectid, newGroupId, oldGroupId,
				Long.parseLong(profile.getId()));
		return observation;
	}

	@GET
	@Path("/recommendationVotes")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getRecommendationVotes(@QueryParam("obvIds") String obvs) {
		//CommonProfile profile = AuthUtils.currentUser(request);
		//System.out.println("testing lock permission");
		//System.out.println("testing lock permission");
		//System.out.println("testing lock permission"+profile.isPresent());
		//System.out.println("testing lock permission"+profileM.get(true));
		//final CommonProfile profile = profileM.get(true).get();
		//System.out.println("testing lock permission "+profile.get().getId());
		//System.out.println("testing lock permission "+profileM);
		Map<String, Object> recoVotes = observationService.getRecommendationVotes(obvs);
		return recoVotes;

	}

}
