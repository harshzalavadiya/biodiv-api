package biodiv.follow;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/follow")
public class FollowController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private FollowService followService;
	
	@GET
	@Path("/random")
	@Produces(MediaType.APPLICATION_JSON)
	public String list() {
		return "abc";
	}
}
