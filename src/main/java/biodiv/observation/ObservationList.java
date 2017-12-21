package biodiv.observation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import biodiv.maps.MapBoolQuery;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapIntegrationService;
import biodiv.maps.MapRangeQuery;
import biodiv.maps.MapResponse;
import biodiv.maps.MapService;

public class ObservationList implements MapService {

	@Override
	public MapResponse create(String index, String type, String documentId, String document) {
		// TODO Auto-generated method stub
		String url="http://localhost:8080/naksha/services/"+index+"/"+type+"/"+documentId;
		MapIntegrationService mapIntegrationService =new MapIntegrationService();
		
		MapHttpResponse res = mapIntegrationService.postRequest(url, document);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(res.getMessage(), MapResponse.class);
		} catch(IOException e) {
			
		}
		return null;
	}

	@Override
	public String fetch(String index, String type, String documentId) {
		// TODO Auto-generated method stub
		MapIntegrationService mapIntegrationService =new MapIntegrationService();
		MapHttpResponse res = mapIntegrationService.fetch(index,type,documentId);
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
