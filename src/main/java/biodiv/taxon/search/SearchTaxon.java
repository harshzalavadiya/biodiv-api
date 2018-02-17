package biodiv.taxon.search;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import biodiv.esclient.ESClientProvider;
import biodiv.esclient.ElasticSearchClient;


public class SearchTaxon {


	private final ElasticSearchClient client = ESClientProvider.getClient();

	public Object search(String data) {

		SearchRequest searchRequest = new SearchRequest("taxon"); 
		searchRequest.types("taxon");
	
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		sourceBuilder.query(QueryBuilders.matchQuery("name", data)); 
		//sourceBuilder.sort(new FieldSortBuilder("name").order(org.elasticsearch.search.sort.SortOrder.ASC));
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse=null;
		try {
			 searchResponse = client.search(searchRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		List<Map<String, Object>> esData = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : searchHits) {
			esData.add(hit.getSourceAsMap());
		}
		
		
		return esData;
		
		

	}
}