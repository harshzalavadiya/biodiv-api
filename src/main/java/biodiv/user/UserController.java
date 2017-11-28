package biodiv.user;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupService;

@Path("/user")
public class UserController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	UserService userService = new UserService();
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> list() {
		List<User> users = userService.findAll(10,0);
		return users;
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public User show(@PathParam("id") long id) {
		User user = userService.findById(Long.valueOf(id));
		System.out.println(user);
		return user;
	}
	
	@GET
	@Path("/currentUserUserGroups")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients="headerClient", authorizers = "isAuthenticated")
	public List<UserGroup> currentUserUserGroups(@Pac4JProfile CommonProfile profile){
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(profile.getId());
		System.out.println("###################");
		UserGroupService userGroupService = new UserGroupService();
		List<UserGroup>  usrGrps = userGroupService.userUserGroups(Long.parseLong(profile.getId()));
		return usrGrps;
	}
}
