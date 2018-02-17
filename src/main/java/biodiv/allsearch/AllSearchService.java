package biodiv.allsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalOrder.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import biodiv.esclient.ESClientProvider;
import biodiv.esclient.ElasticSearchClient;

public class AllSearchService {

	private final ElasticSearchClient client = ESClientProvider.getClient();
	
	
	public AllSearchResponse search(String speciesname, String location, String license,String query,String name, String contributor, String tag, String content, String attribution, String participants) {
		// TODO Auto-generated method stub
		
		//for seacrhing in all indices
		SearchRequest searchRequest = new SearchRequest(); 
		
		/**
		 * SearchSourceBuilder created for searches
		 */
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		
		BoolQueryBuilder boolQuery = null;
		BoolQueryBuilder masterBoolQuery = QueryBuilders.boolQuery();
		
		if(location!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("location", location));
			boolQuery.should(QueryBuilders.matchQuery("placename", location));
			masterBoolQuery.must(boolQuery);
		}
		if(license!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("license.firstValue", license));
			boolQuery.should(QueryBuilders.matchQuery("license", license));
			boolQuery.should(QueryBuilders.matchQuery("licensename", license));
			masterBoolQuery.must(boolQuery);
		}
		if(query!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("all", query));
			masterBoolQuery.must(boolQuery);
		}
		if(name!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("name", name));
			boolQuery.should(QueryBuilders.matchQuery("email", name));
			boolQuery.should(QueryBuilders.matchQuery("username", name));
			boolQuery.should(QueryBuilders.matchQuery("sitename", name));
			boolQuery.should(QueryBuilders.matchQuery("text", name));
			boolQuery.should(QueryBuilders.matchQuery("title", name));
			boolQuery.should(QueryBuilders.matchQuery("member", name));
			boolQuery.should(QueryBuilders.matchQuery("group_webaddress", name));
			masterBoolQuery.must(boolQuery);
		}
		
		if(contributor!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("authorname", contributor));
			boolQuery.should(QueryBuilders.matchQuery("contributor.value", contributor));
			boolQuery.should(QueryBuilders.matchQuery("username", contributor));
			boolQuery.should(QueryBuilders.matchQuery("username", contributor));
			boolQuery.should(QueryBuilders.matchQuery("member", contributor));
			masterBoolQuery.must(boolQuery);
		}
		if(tag!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("tag", tag));
			masterBoolQuery.must(boolQuery);
		}
		if(attribution!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("attribution", attribution));
			boolQuery.should(QueryBuilders.matchQuery("group_webaddress", name));
			masterBoolQuery.must(boolQuery);
		}
		if(participants!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("participants", participants));
			boolQuery.should(QueryBuilders.matchQuery("member", participants));
			masterBoolQuery.must(boolQuery);
		}
		
		sourceBuilder.query(masterBoolQuery); 
		
		sourceBuilder.from(0); 
		sourceBuilder.size(10); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder
		.aggregation(AggregationBuilders.filters("user",QueryBuilders.typeQuery("user")))
		.aggregation(AggregationBuilders.filters("newsletter",QueryBuilders.typeQuery("newsletter")))
		.aggregation(AggregationBuilders.filters("resource",QueryBuilders.typeQuery("resource")))
		.aggregation(AggregationBuilders.filters("documents",QueryBuilders.typeQuery("documnets")))
		.aggregation(AggregationBuilders.filters("usergroup",QueryBuilders.typeQuery("usergroup")))
		.aggregation(AggregationBuilders.filters("observations",QueryBuilders.typeQuery("observations")))
		.aggregation(AggregationBuilders.filters("species",QueryBuilders.typeQuery("species")))
		.aggregation(AggregationBuilders.filters("project",QueryBuilders.typeQuery("project")));
		searchRequest.source(sourceBuilder);
		
		List<Map<String,Object>> eData=new ArrayList<Map<String, Object>>();
		SearchHits hits=null;
		Aggregations aggregations=null;
		try {
			
			SearchResponse searchResponse = client.search(searchRequest);
			 hits = searchResponse.getHits();
			  aggregations= searchResponse.getAggregations();
			  
	    	long totalHits = hits.getTotalHits();
	    	SearchHit[] searchHits = hits.getHits();
	    	for (SearchHit hit : searchHits) {
	    	    // do something with the SearchHit
	    		 eData.add(hit.getSourceAsMap());
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, aggregations.getAsMap());
		
	}


	public AllSearchResponse usergroupSearch(String location, String participants, String title, String pages) {
		// TODO Auto-generated method stub
		SearchRequest searchRequest = new SearchRequest("usergroup"); 
		searchRequest.types("usergroup"); 
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		BoolQueryBuilder boolQuery = null;
		BoolQueryBuilder masterBoolQuery = QueryBuilders.boolQuery();
		
		if(title!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("title", title));
			 masterBoolQuery.must(boolQuery);
		}
		if(pages!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("pages", pages));
			masterBoolQuery.must(boolQuery);
		}
		sourceBuilder.query(masterBoolQuery); 
		
		sourceBuilder.from(0); 
		sourceBuilder.size(10); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(sourceBuilder);
		List<Map<String,Object>> eData=new ArrayList<Map<String, Object>>();
		SearchHits hits=null;
		
		try {
			
			SearchResponse searchResponse = client.search(searchRequest);
			 hits = searchResponse.getHits();
			 
			  
	    	long totalHits = hits.getTotalHits();
	    	SearchHit[] searchHits = hits.getHits();
	    	for (SearchHit hit : searchHits) {
	    	    // do something with the SearchHit
	    		 eData.add(hit.getSourceAsMap());
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, null);
	}


	public AllSearchResponse userSearch(String location, String content, String name) {
		SearchRequest searchRequest = new SearchRequest("user"); 
		searchRequest.types("user"); 
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		BoolQueryBuilder boolQuery = null;
		BoolQueryBuilder masterBoolQuery = QueryBuilders.boolQuery();
		
		if(name!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("name", name));
			boolQuery.should(QueryBuilders.matchQuery("email", name));
			 masterBoolQuery.must(boolQuery);
		}
		
		sourceBuilder.query(masterBoolQuery); 
		
		sourceBuilder.from(0); 
		sourceBuilder.size(10); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(sourceBuilder);
		List<Map<String,Object>> eData=new ArrayList<Map<String, Object>>();
		SearchHits hits=null;
		
		try {
			
			SearchResponse searchResponse = client.search(searchRequest);
			 hits = searchResponse.getHits();
			 
			  
	    	long totalHits = hits.getTotalHits();
	    	SearchHit[] searchHits = hits.getHits();
	    	for (SearchHit hit : searchHits) {
	    	    // do something with the SearchHit
	    		 eData.add(hit.getSourceAsMap());
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, null);
	}


	public AllSearchResponse documentSearch(String speciesname, String location, String license, String query,
			String name, String contributor, String tag, String content, String attribution, String participants,
			String doctype) {
		// TODO Auto-generated method stub
		SearchRequest searchRequest = new SearchRequest("documents"); 
		searchRequest.types("documents"); 
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
		BoolQueryBuilder boolQuery = null;
		BoolQueryBuilder masterBoolQuery = QueryBuilders.boolQuery();
		
		if(doctype!=null){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("doc_type", doctype));
			 masterBoolQuery.must(boolQuery);
		}
		
		sourceBuilder.query(masterBoolQuery); 
		
		sourceBuilder.from(0); 
		sourceBuilder.size(10); 
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
		searchRequest.source(sourceBuilder);
		
		List<Map<String,Object>> eData=new ArrayList<Map<String, Object>>();
		SearchHits hits=null;
		Aggregations aggregations=null;
		try {
			
			SearchResponse searchResponse = client.search(searchRequest);
			 hits = searchResponse.getHits();
			  aggregations= searchResponse.getAggregations();
			  
	    	long totalHits = hits.getTotalHits();
	    	SearchHit[] searchHits = hits.getHits();
	    	for (SearchHit hit : searchHits) {
	    	    // do something with the SearchHit
	    		 eData.add(hit.getSourceAsMap());
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, null);
	}

}
