package biodiv.observation;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
		
		System.out.println("controller mai aaye");
		 List<Map<String, Object>> obvs=null;
		 obvs = observationService.list();
	
		return obvs;
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public Observation show(@PathParam("id") long id) {
		DataObject obv = null;
		try {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Observation) obv;
	}

	@GET
	@Path("/{id}/userGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroup> obvUserGroups(@PathParam("id") long id) {
		List<UserGroup> usrGrps = observationService.obvUserGroups(id);
		return usrGrps;
	}

}
