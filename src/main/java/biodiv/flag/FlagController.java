package biodiv.flag;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import biodiv.user.User;
import biodiv.userGroup.UserGroupService;

@Path("/flag")
public class FlagController {
	
	@Inject
	FlagService flagService;
	
	@GET
	@Path("/olderFlags")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Flag> fetchOlderFlags(@QueryParam("objectType") String objectType , @QueryParam("objectId") long objectId) {
		List<Flag> flags = flagService.fetchOlderFlags(objectType, objectId);
		return flags;
	}

}