package biodiv.common;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;

@Path("/species")

public class SpeciesGroupController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	SpeciesGroupService speciesGroupService;
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public List<SpeciesGroup> list(){
		List<SpeciesGroup> results= speciesGroupService.list();
		return results;
	}
}

