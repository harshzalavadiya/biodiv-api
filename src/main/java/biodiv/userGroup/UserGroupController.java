package biodiv.userGroup;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import biodiv.Transactional;
import biodiv.user.User;

@Path("/userGroup")
public class UserGroupController {
	
	@Inject
	UserGroupService userGroupService;

	@Context
	private ResourceContext resourceContext;

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroup> list(@QueryParam("max") int max, @QueryParam("offset") int offset) {
		List<UserGroup> usrGrp = null;
		if (max == 0 && offset == 0) {
			usrGrp = userGroupService.findAll();
		} else if (max != 0 && offset == 0) {
			usrGrp = userGroupService.findAll(max, 0);
		} else {
			usrGrp = userGroupService.findAll(max, offset);
		}
		return usrGrp;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public UserGroup show(@PathParam("id") long id) {
		UserGroup usrGrp = userGroupService.findById(Long.valueOf(id));
		return usrGrp;
	}

	@GET
	@Path("/find/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public UserGroup show(@PathParam("name") String name) {
		UserGroup usrGrp = userGroupService.findByName(name);
		return usrGrp;
	}

	@GET
	@Path("/{groupId}/users/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> listofUsers(@PathParam("groupId") long groupId, @PathParam("roleId") long roleId) {
		List<User> users = userGroupService.userList(groupId, roleId);
		return users;
	}

	@POST
	@Path("/bulkPost")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	@Transactional
	public String bulkPost(@QueryParam("pullType") String pullType, @QueryParam("selectionType") String selectionType,
			@QueryParam("objectType") String objectType, @QueryParam("objectIds") String objectIds,
			@QueryParam("submitType") String submitType, @QueryParam("userGroups") String userGroups,
			@QueryParam("filterUrl") String filterUrl, @Pac4JProfile CommonProfile profile)
			throws NumberFormatException, Exception {
		String msg = userGroupService.posttoGroups(objectType, pullType, submitType, objectIds, userGroups,
				Long.parseLong(profile.getId()), filterUrl);
		return msg;
	}

	@Path("/{groupName}/{x}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object mapfunc(@PathParam("x") String x) {
		Mapping m = new Mapping(x);

		return m.getObject();
	}

}