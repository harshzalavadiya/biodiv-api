package biodiv.speciesGroup;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.SpeciesGroup;

@Path("/species")

public class SpeciesGroupController {
	private final Logger log = LoggerFactory.getLogger(SpeciesGroup.class);
	
	SpeciesGroupService speciesGroupService=new SpeciesGroupService();
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SpeciesGroup> list(){
		List<SpeciesGroup> results= speciesGroupService.list();
		return results;
	}
}

