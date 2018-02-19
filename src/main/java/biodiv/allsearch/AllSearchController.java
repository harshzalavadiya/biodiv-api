package biodiv.allsearch;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import biodiv.Transactional;
import biodiv.observation.ObservationList;
import biodiv.user.UserController;



@Path("/search")
public class AllSearchController {

private final Logger log = LoggerFactory.getLogger(AllSearchController.class);
private final AllSearchService allSearchService=new AllSearchService();
	
	/**
	 * 
	 * @param index
	 * The this going to allow admin to upload setting and mapping 
	 */
	@GET
	@Path("/all")
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public AllSearchResponse search(
			@QueryParam("module") String module,
			@QueryParam("speciesname") String speciesname,
			@QueryParam("location") String location,
			@QueryParam("license") String license,
			@QueryParam("query") String query,
			@QueryParam("name") String name,
			@QueryParam("contributor") String contributor,
			@QueryParam("tag") String tag,
			@QueryParam("content") String content,
			@QueryParam("attribution") String attribution,
			@QueryParam("participants") String participants,
			@QueryParam("title") String title,
			@QueryParam("pages") String pages,
			@QueryParam("doctype") String doctype,
			@DefaultValue("0") @QueryParam("from") Integer from,
			@DefaultValue("10") @QueryParam("limit") Integer limit
			){
		
		if(module.equalsIgnoreCase("all")){
			return allSearchService.search(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,from, limit);	
			
		}
		else if(module.equalsIgnoreCase("observations")){
			return allSearchService.search(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,from,limit);
		}
		else if(module.equalsIgnoreCase("species")){
			return allSearchService.search(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,from,limit);
		}
		else if(module.equalsIgnoreCase("usergroups")){
			return allSearchService.usergroupSearch(location,participants,title,pages,from,limit);
		}
		else if(module.equalsIgnoreCase("users")){
			return allSearchService.userSearch(location,content,name,from,limit);
		}
		else if(module.equalsIgnoreCase("documents")){
			return  allSearchService.documentSearch(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,doctype,from,limit);
		}
		else if(module.equalsIgnoreCase("newsletter")){
			return allSearchService.search(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,from,limit);
		}
		else if(module.equalsIgnoreCase("resouces")){
			return allSearchService.search(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,from,limit);
		}
		else{
			return allSearchService.search(speciesname,location,license,query,name,contributor,tag,content,attribution,participants,from,limit);
		}	
	}
}
