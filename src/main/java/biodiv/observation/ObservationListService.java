package biodiv.observation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.maps.MapBiodivResponse;
import biodiv.maps.MapDocument;
import biodiv.common.NakshaUrlService;
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

    @Inject
    NakshaUrlService nakshaUrlService;

    @Inject
	MapIntegrationService mapIntegrationService;

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
	public MapBiodivResponse search(String index, String type, MapSearchQuery querys, String geoAggregationField, Integer geoAggegationPrecision, Boolean onlyFilteredAggregation, String termsAggregationField) {
		String newurl= config.getString("nakshaUrl")+"/services/search/" + index + "/" + type+"?geoAggregationField="+geoAggregationField +"&geoAggegationPrecision="+geoAggegationPrecision;
		log.debug("Searching at : {}", newurl);
		if(onlyFilteredAggregation != null && onlyFilteredAggregation == true) {
			newurl += "&onlyFilteredAggregation=true";
		}
		if(termsAggregationField != null) {
			newurl += "&termsAggregationField=" + termsAggregationField;
		}
		
		return mapIntegrationService.postSearch(newurl,querys);

	}

	@Override
	public MapDocument termsAggregation(String index, String type, String field, String locationField, Integer size, MapSearchQuery mapSearchQuery) {
		String url = nakshaUrlService.getTermsAggregationUrl(index, type, field, locationField, size);
		MapHttpResponse mapHttpResponse = mapIntegrationService.postRequest(url, mapSearchQuery);
		MapDocument mapDocument = new MapDocument();
		mapDocument.setDocument(mapHttpResponse.getDocument().toString());
		return mapDocument;
	}
	
	public void uploadSettingsAndMappings(String index, String settingsAndMappings) {
		// TODO Auto-generated method stub
		String newurl=config.getString("nakshaUrl")+"/services/mapping/"+index;
		MapIntegrationService mapIntegrationService = new MapIntegrationService();
		MapHttpResponse mapHttpResponse= mapIntegrationService.uploadSettingAndMappings(newurl,settingsAndMappings);
		
	}

}
