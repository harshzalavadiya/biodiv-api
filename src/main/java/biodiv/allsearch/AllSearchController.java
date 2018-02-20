package biodiv.allsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
			@QueryParam("speciesname") String speciesname,
			@QueryParam("location") String location,
			@QueryParam("license") String license,
			@QueryParam("query") String query,
			@QueryParam("name") String name,
			@QueryParam("contributor") String contributor,
			@QueryParam("tag") String tag,
			@QueryParam("text") String content,
			@QueryParam("attribution") String attribution,
			@QueryParam("member") String participants,
			@QueryParam("title") String title,
			@QueryParam("pages") String pages,
			@QueryParam("doctype") String doctype,
			@DefaultValue("0") @QueryParam("from") Integer from,
			@DefaultValue("12") @QueryParam("limit") Integer limit,
			@QueryParam("object_type") String object_type
			){
		List<String> listData=new ArrayList<>();
		if(object_type!=null){
			String [] objectType = object_type.split("OR");
			
			for(String data:objectType){
				listData.add(data.trim().toLowerCase());
				
			}
		}
		if(listData.size()==1 && listData.contains("observation")){
			return allSearchService.obvsearch(listData,speciesname,location,license,query,name,contributor,tag,content,
					attribution,participants,title,pages,doctype,from, limit);	
		}
		
		return allSearchService.search(listData,speciesname,location,license,query,name,contributor,tag,content,
				attribution,participants,title,pages,doctype,from, limit);	
			
	}
}
