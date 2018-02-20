package biodiv.user;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.AuthUtils;
import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupService;

@Path("/user")
public class UserController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	@Inject
	UserService userService;

	@Inject
	UserGroupService userGroupService;

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> list() {
		List<User> users = userService.findAll(10, 0);
		return users;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public User show(@PathParam("id") long id) {
		User user = userService.findById(Long.valueOf(id));
		return user;
	}

	@GET
	@Path("/currentUserUserGroups")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	public List<UserGroup> currentUserUserGroups(@Context HttpServletRequest request) {
		CommonProfile profile = AuthUtils.currentUser(request);
		log.debug("Getting usergroups for current user ", profile);
		List<UserGroup> usrGrps = userGroupService.userUserGroups(Long.parseLong(profile.getId()));
		return usrGrps;
	}
}
