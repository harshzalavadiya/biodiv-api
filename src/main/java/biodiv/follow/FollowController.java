package biodiv.follow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



@Path("/follow")
public class FollowController {

	@GET
	@Path("/random")
	@Produces(MediaType.APPLICATION_JSON)
	public String list(){
		FollowService followService = new FollowService();
		return "abc";
	}
}
