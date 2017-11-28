package biodiv.observation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import biodiv.userGroup.UserGroup;

@Path("/observation")
public class ObservationController {
	
	@Inject
	ObservationService observationService;
	
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Observation> list(@PathParam("groupName") String grpName) {
		List<Observation> obvs;
		System.out.println("aaaaaaaaaaaa");
		System.out.println(grpName);
		if(grpName == null){
			obvs = observationService.findAll(3,0);
		}else{
			obvs = observationService.findAllByGroup(3,0);
		}
		
		return obvs;
	}
	
	
	@GET
	@Path("/{id}/userGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroup> obvUserGroups(@PathParam("id") long id){
		List<UserGroup> usrGrps = observationService.obvUserGroups(id);
		return usrGrps;
	}
	
}
