package biodiv.observation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/observation")
public class ObservationController {
	
	ObservationService observationService = new ObservationService();
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Observation> list() {
		List<Observation> obvs = observationService.findAll(3, 0);
		return obvs;
	}
}
