package biodiv.observation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.maps.MapBiodivResponse;
import biodiv.maps.MapAndBoolQuery;
import biodiv.maps.MapHttpResponse;
import biodiv.maps.MapIntegrationService;
import biodiv.maps.MapAndRangeQuery;
import biodiv.maps.MapResponse;
import biodiv.maps.MapSearchQuery;
import biodiv.maps.MapService;

import javax.inject.Inject;

public class ObservationListService implements MapService {


	public static final String URL = "http://localhost:8081/";
	private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
	Configuration config;


	@Override
	public MapResponse create(String index, String type, String documentId, String document) {

		// TODO Auto-generated method stub

		String newurl = config.getString("nakshaUrl")+"/services/data/" + index + "/" + type + "/" + documentId;

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
		String newurl = config.getString("nakshaUrl")+"/services/data/" + index + "/" + type + "/" + documentId;
		MapHttpResponse content = mapIntegrationService.getRequest(newurl);
		return content;
	}

	@Override
	public MapResponse delete(String index, String type, String documentId) {
		String newurl = config.getString("nakshaUrl")+"/services/data/"+index + "/" + type+"/"+documentId;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapResponse mapResponse=mapIntegrationService.deleteSingleDocument(newurl);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapResponse update(String index, String type, String documentId, String document) {
		String newurl = config.getString("nakshaUrl")+"/services/data/"+index + "/" + type+"/"+documentId;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
			MapResponse mapResponse=mapIntegrationService.updateSingleDocument(newurl,document);
			

		
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
		String newurl = config.getString("nakshaUrl")+"/services/term-search/" + index + "/" + type+"?"+"key="+key+"&value="+value;
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
	public MapBiodivResponse search(String index, String type, MapSearchQuery querys, Integer max, Integer offset, String sortOn, String geoAggregationField, Integer geoAggegationPrecision,Double left, Double right, Double top, Double bottom, Boolean onlyFilteredAggregation) {
		String newurl= config.getString("nakshaUrl")+"/services/search/" + index + "/" + type+"?from="+offset+"&limit="+max
				+"&geoAggregationField="+geoAggregationField +"&geoAggegationPrecision="+geoAggegationPrecision+"&sortOn="+sortOn+"&sortType=DESC";
		
		log.debug("Searching at : {}", newurl);
		
		if(left!=null &&right!=null && top!=null && bottom!=null){
			 newurl += "&top="+top+"&bottom="+bottom+"&left="+left+"&right="+right;
		}
		
		if(onlyFilteredAggregation != null && onlyFilteredAggregation == true) {
			newurl += "&onlyFilteredAggregation=true";
		}
		
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapBiodivResponse mapHttpResponse= mapIntegrationService.postSearch(newurl,querys);
		
		return mapHttpResponse;
//		} catch(Exception e) {
//			e.printStackTrace();
//			ResponseModel responseModel = new ResponseModel(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
//		}
	}

	
	
	public void uploadSettingsAndMappings(String index, String settingsAndMappings) {
		// TODO Auto-generated method stub
		String newurl=config.getString("nakshaUrl")+"/services/mapping/"+index;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapHttpResponse mapHttpResponse= mapIntegrationService.uploadSettingAndMappings(newurl,settingsAndMappings);
		
	}

}
