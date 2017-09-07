package biodiv.auth;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

import org.pac4j.jax.rs.annotations.Pac4JLogout;

@Path("/logout")
public class LogoutController {
	
	@DELETE
	@Pac4JLogout(skipResponse = true)
	public void logout() {
		// do nothing
	}
	
}
