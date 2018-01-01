package biodiv.observation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import biodiv.common.CommonMethod;
import biodiv.maps.MapBoolQuery;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapResponse;

@Path("/maps")
public class ObservationListController {
	@POST
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapResponse list(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("documentId") String documentId, String document) {
		
		ObservationList observationList = new ObservationList();

		MapResponse mapResponse = observationList.create(index, type, documentId, document);

		return mapResponse;
	}

	@GET
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapHttpResponse list(@PathParam("index") String index, @PathParam("type") String type,
			@PathParam("documentId") String documentId) {
		System.out.println(index);
		ObservationList observationList = new ObservationList();

		MapHttpResponse content = observationList.fetch(index, type, documentId);
		return content;
	}

	@GET
	@Path("/{index}/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapHttpResponse list(
			@PathParam("index") String index, 
			@PathParam("type") String type,
			@DefaultValue("")@QueryParam("sGroup") String sGroup,
			@DefaultValue("")@QueryParam("taxon") String taxon,
			@DefaultValue("")@QueryParam("user") String user,
			@DefaultValue("")@QueryParam("userGroupList") String userGroupList,
			@DefaultValue("")@QueryParam("webaddress") String webaddress,
			@DefaultValue("")@QueryParam("speciesName") String speciesName,
			@DefaultValue("")@QueryParam("mediaFilter") String mediaFilter,
			@DefaultValue("")@QueryParam("months") String months,
			@DefaultValue("1970")@QueryParam("minYear") Integer minYear,
			@QueryParam("maxYear") Integer maxYear,
			@DefaultValue("1")@QueryParam("minDay") Integer minDay,
			@DefaultValue("31")@QueryParam("maxDay") Integer maxDay,
			@DefaultValue("265799")@QueryParam("classifdication") String classificationid,
			@DefaultValue("lastrevised")@QueryParam("sort") String sort,
			@DefaultValue("10")@QueryParam("max") Integer max,
			@DefaultValue("0") @QueryParam("offset") Integer offset
			) {

		List<MapBoolQuery> queryLists = new ArrayList<MapBoolQuery>();

		CommonMethod commonMethod = new CommonMethod();

		Set<String> groupId = commonMethod.cSTSOT(sGroup);
		if(!groupId.isEmpty()){
			System.out.println(groupId.size());
			queryLists.add(new MapBoolQuery("speciesgroupid", groupId));
		}
		
		
		Set<String> taxonId = commonMethod.cSTSOT(taxon);
		if(!taxonId.isEmpty()){
			queryLists.add(new MapBoolQuery("path", taxonId));
		}
		
		Set<String> authorId = commonMethod.cSTSOT(user);
		
		if(!authorId.isEmpty()){
			System.out.println(authorId.size());
			queryLists.add(new MapBoolQuery("authorid", authorId));
		}
		

		
		Set<String> userGroupId = commonMethod.cSTSOT(userGroupList);
		if(!userGroupId.isEmpty()){
			queryLists.add(new MapBoolQuery("usergroupid", userGroupId));
		}
		

	
		Set<String> userGroupName = commonMethod.cSTSOT(webaddress);
		if(!userGroupName.isEmpty()){
			queryLists.add(new MapBoolQuery("usergroupname", userGroupName));

		}
		if(maxYear==null){
			maxYear=Calendar.getInstance().get(Calendar.YEAR);
		}
		
		ObservationList observationList = new ObservationList();
		MapHttpResponse mapHttpResponse = observationList.search(index, type, queryLists, max, offset);
		
		return mapHttpResponse;
	}

}
