package biodiv.traits;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Intercept;
import biodiv.common.SpeciesGroup;

@Path("/trait")

public class TraitController {
	
	TraitService traitService=new TraitService();
	
	
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Intercept
	public List<Object[]> list(){
		List<Object[]> traitList=traitService.list();
		return traitList;
	}
}
