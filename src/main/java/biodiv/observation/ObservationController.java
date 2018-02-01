package biodiv.observation;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

import biodiv.Intercept;
import biodiv.common.DataObject;
import biodiv.userGroup.UserGroup;

@Path("/observation")
public class ObservationController {

	@Inject
	ObservationService observationService;

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public List<Map<String, Object>> list() {
		List<Map<String, Object>> obvs = null;

		try {
			obvs = observationService.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obvs;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public Observation show(@PathParam("id") long id) {
		
		try {
		Observation observation= observationService.show(id);
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
	@Intercept
	public List<Map<String,Object>> getCustomFields(@QueryParam("obvId") Long obvId){
		
		List<Map<String,Object>> cf = observationService.getCustomFields(obvId);
		return cf;
	}
	
	@POST
	@Path("/updateCustomField")
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients="headerClient", authorizers = "isAuthenticated")
	@Intercept
	public String updateCustomField(@QueryParam("fieldValue") String fieldValue, @QueryParam("cfId") Long cfId,
			@QueryParam("obvId") Long obvId,@Pac4JProfile CommonProfile profile){
		
		
		String msg = observationService.updateInlineCf(fieldValue,cfId,obvId,Long.parseLong(profile.getId()));
		return msg;
	}
	
	@GET
	@Path("/resource/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public List<ObservationResource> getResource(@PathParam("id") long id) {
		List<ObservationResource> observationResources = observationService.getResouce(id);
		return observationResources;
	}
	@POST
	@Path("/updategroup")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	@Pac4JSecurity(clients = "headerClient", authorizers = "isAuthenticated")
	public Object updateGroup(@QueryParam("objectid") Long objectid,@QueryParam("groupid") Long groupid){
		
		Object observation=observationService.updateGroup(objectid,groupid);
		return observation;
	}

}
