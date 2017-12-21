package biodiv.observation;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import biodiv.Intercept;
import biodiv.maps.MapResponse;

@Path("/maps")
public class ObservationListController {
	@POST
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MapResponse list(@PathParam("index") String index,@PathParam("type") String type,@PathParam("documentId") String documentId,@QueryParam("document") String document) {
		
		ObservationList observationList =new ObservationList();
			MapResponse mapResponse=observationList.create(index, type, documentId, document);
		return mapResponse ;
	}
	@GET
	@Path("/{index}/{type}/{documentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String list(@PathParam("index") String index,@PathParam("type") String type,@PathParam("documentId") String documentId) {
		
			ObservationList observationList =new ObservationList();
			String mapResponse=observationList.fetch(index, type, documentId);
		return mapResponse ;
	}
	

}
