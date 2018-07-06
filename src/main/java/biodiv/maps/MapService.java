package biodiv.maps;

import java.util.List;

/**
 * 
 * The services available from map module
 */
public interface MapService {

	public MapResponse create(String index, String type, String documentId, String document);

	public MapHttpResponse fetch(String index, String type, String documentId);

	public MapResponse delete(String index, String type, String documentId);

	public MapResponse update(String index, String type, String documentId, String document);

	public List<MapResponse> bulkUpload(String index, String type, String jsonArray);

	public MapHttpResponse termSearch(String index, String type, String key, String value, Integer from, Integer limit);

	public MapBiodivResponse search(String index, String type, MapSearchQuery query, String geoAggregationField,
			Integer geoAggegationPrecision, Boolean onlyFilteredAggregation, String termsAggregationField);

	public List<String> searchBool(String index, String type, List<MapAndBoolQuery> queries, Integer from,
			Integer limit);

	public List<String> searchRange(String index, String type, List<MapAndRangeQuery> queries, Integer from,
			Integer limit);

	MapDocument termsAggregation(String index, String type, String field, String locationField, Integer size,
			MapSearchQuery mapSearchQuery);
}
