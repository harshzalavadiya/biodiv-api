package biodiv.observation;

import java.util.List;

import biodiv.maps.MapBoolQuery;
import biodiv.maps.MapIntegrationService;
import biodiv.maps.MapRangeQuery;
import biodiv.maps.MapResponse;
import biodiv.maps.MapService;

public class ObservationList implements MapService {

	@Override
	public MapResponse create(String index, String type, String documentId, String document) {
		// TODO Auto-generated method stub
		String url="http://localhost:8080/"+index+"/"+type+"/"+documentId;
		MapIntegrationService mapIntegrationService =new MapIntegrationService();
		
		MapResponse mapResponse=new MapResponse(mapIntegrationService.postRequest(url, document),"created");
		return mapResponse;
	}

	@Override
	public String fetch(String index, String type, String documentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapResponse delete(String index, String type, String documentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapResponse update(String index, String type, String documentId, String document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MapResponse> bulkUpload(String index, String type, String jsonArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> termSearch(String index, String type, String key, String value, Integer from, Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> searchBool(String index, String type, List<MapBoolQuery> queries, Integer from, Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> searchRange(String index, String type, List<MapRangeQuery> queries, Integer from,
			Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
