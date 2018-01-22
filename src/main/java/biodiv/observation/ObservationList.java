package biodiv.observation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import biodiv.maps.MapBiodivResponse;
import biodiv.maps.MapAndBoolQuery;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapIntegrationService;
import biodiv.maps.MapAndRangeQuery;
import biodiv.maps.MapResponse;
import biodiv.maps.MapSearchQuery;
import biodiv.maps.MapService;

public class ObservationList implements MapService {

	private static final String URL = "http://localhost:8080/naksha/services/data/";

	@Override
	public MapResponse create(String index, String type, String documentId, String document) {

		// TODO Auto-generated method stub

		String newurl = URL + index + "/" + type + "/" + documentId;

		/**
		 * Map integration service have required method to make respective calls
		 */

		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		/**
		 * Wrapper class for observation
		 */
		ObservationListObject object = new ObservationListObject();
		object.setDocument(document);

		/**
		 * MapResponse is generic class to handle all the responses
		 */

		MapHttpResponse res = mapIntegrationService.postRequest(newurl, object);
		/**
		 * ObjectMapper provided by the jackson for serializing and
		 * deserializing the java object to JSON.
		 */
		ObjectMapper mapper = new ObjectMapper();
		try {
//			return mapper.readValue(res.getDocument(), MapResponse.class);
			
			
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public MapHttpResponse fetch(String index, String type, String documentId) {
		// // TODO Auto-generated method stub
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		String newurl = URL + index + "/" + type + "/" + documentId;
		MapHttpResponse content = mapIntegrationService.getRequest(newurl);
		return content;
	}

	@Override
	public MapResponse delete(String index, String type, String documentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapResponse update(String index, String type, String documentId, String document) {
		String newurl = URL+index + "/" + type+"/"+documentId;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
			MapResponse mapResponse=mapIntegrationService.updateSingleDocument(newurl,document);
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public List<MapResponse> bulkUpload(String index, String type, String jsonArray) {
		// TODO Auto-generated method stub
		return null;
	}
/**
 * For single id observation object search
 */
	@Override
	public MapHttpResponse termSearch(String index, String type, String key, String value, Integer from, Integer limit) {
		// TODO Auto-generated method stub
		String newurl = "http://localhost:8080/naksha/services/term-search/" + index + "/" + type+"?"+"key="+key+"&value="+value;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapHttpResponse mapHttpResponse= mapIntegrationService.getSingleSearch(newurl);	
		return mapHttpResponse;
	}

	@Override
	public List<String> searchBool(String index, String type, List<MapAndBoolQuery> queries, Integer from, Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> searchRange(String index, String type, List<MapAndRangeQuery> queries, Integer from,
			Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public MapBiodivResponse search(String index, String type, MapSearchQuery querys, Integer max, Integer offset, String sortOn, String geoAggregationField, Integer geoAggegationPrecision) {
		// TODO Auto-generated method stub
		String newurl = "http://localhost:8080/naksha/services/search/" + index + "/" + type+"?from="+offset+"&limit="+max
				+"&geoAggregationField="+geoAggregationField + "&geoAggegationPrecision="+geoAggegationPrecision;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapBiodivResponse mapHttpResponse= mapIntegrationService.postSearch(newurl,querys);
		
		return mapHttpResponse;
	}

	
	
	public void uploadSettingsAndMappings(String index, String settingsAndMappings) {
		// TODO Auto-generated method stub
		String newurl="htpp://localhost:8080/naksha/services/mapping/"+index;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapHttpResponse mapHttpResponse= mapIntegrationService.uploadSettingAndMappings(newurl,settingsAndMappings);
		
	}

}
