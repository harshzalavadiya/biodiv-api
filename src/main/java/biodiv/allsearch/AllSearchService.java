package biodiv.allsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.Filters.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import biodiv.esclient.ESClientProvider;
import biodiv.esclient.ElasticSearchClient;

public class AllSearchService {

	private final ElasticSearchClient client = ESClientProvider.getClient();
	
	
	public AllSearchResponse search(String speciesname, String location, String license,String query,String name, String contributor, String tag, String content, String attribution, String participants,Integer from,Integer limit ) {
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
		if(from!=null){
			sourceBuilder.from(from); 
		}
		if(limit!=null){
			sourceBuilder.size(limit); 
		}
		
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
		List<Map<String,Object>> agg=new ArrayList<Map<String,Object>>();
		try {
			
			SearchResponse searchResponse = client.search(searchRequest);
			 hits = searchResponse.getHits();
			  aggregations= searchResponse.getAggregations();
			  
			  Map<String, org.elasticsearch.search.aggregations.Aggregation> aggregationMap = aggregations.getAsMap();
			  Filters observations = (Filters) aggregationMap.get("observations");
			  Filters species = (Filters) aggregationMap.get("species");
			  Filters documents = (Filters) aggregationMap.get("documents");
			  Filters newsletter = (Filters) aggregationMap.get("newsletter");
			  Filters user = (Filters) aggregationMap.get("user");
			  Filters resource = (Filters) aggregationMap.get("resource");
			  Filters project = (Filters) aggregationMap.get("project");
			  Filters usergroup = (Filters) aggregationMap.get("usergroup");
			  
			  
			  Bucket observationsB = observations.getBucketByKey("0");
			  Bucket speciesB = species.getBucketByKey("0");
			  Bucket documentsB = documents.getBucketByKey("0");
			  Bucket newsletterB = newsletter.getBucketByKey("0");
			  Bucket userB = user.getBucketByKey("0");
			  Bucket resourceB = resource.getBucketByKey("0");
			  Bucket projectB = project.getBucketByKey("0");
			  Bucket usergroupB = usergroup.getBucketByKey("0");
			  
			  Map<String, Object> obv=new HashMap<String,Object>();
			  obv.put("observations", observationsB.getDocCount());
			  Map<String, Object> spe=new HashMap<String,Object>();
			  spe.put("species", speciesB.getDocCount());
			  Map<String, Object> doc=new HashMap<String,Object>();
			  doc.put("documnets", documentsB.getDocCount());
			  Map<String, Object> news=new HashMap<String,Object>();

			  
			  news.put("newsletter", newsletterB.getDocCount());
			  Map<String, Object> use=new HashMap<String,Object>();
			  use.put("user", userB.getDocCount());
			  
			  Map<String, Object> res=new HashMap<String,Object>();
			  res.put("resource", resourceB.getDocCount());
			  Map<String, Object> pro=new HashMap<String,Object>();
			  pro.put("project", projectB.getDocCount());
			  Map<String, Object> userg=new HashMap<String,Object>();
			  userg.put("usergroup", usergroupB.getDocCount());
			  
			  agg.add(userg);
			  agg.add(res);
			  agg.add(use);
			  agg.add(news);
			  
			  agg.add(spe);
			  agg.add(pro);
			  agg.add(doc);
			  agg.add(obv);
	    	long totalHits = hits.getTotalHits();
	    	SearchHit[] searchHits = hits.getHits();
	    	for (SearchHit hit : searchHits) {
	    	    // do something with the SearchHit
	    		
	    		Map<String,Object> data=new HashMap<String,Object>();
	    		data.put("index",hit.getIndex());
	    		data.put("type",hit.getType());
	    		data.put("id", hit.getId());
	    		eData.add(data);
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, agg);
		
	}


	public AllSearchResponse usergroupSearch(String location, String participants, String title, String pages,Integer from, Integer limit) {
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
		
		sourceBuilder.from(from); 
		sourceBuilder.size(limit); 
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
	    		Map<String,Object> data=new HashMap<String,Object>();
	    		data.put("index",hit.getIndex());
	    		data.put("type",hit.getType());
	    		data.put("id", hit.getId());
	    		 eData.add(data);
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, null);
	}


	public AllSearchResponse userSearch(String location, String content, String name,Integer from, Integer limit) {
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
		
		sourceBuilder.from(from); 
		sourceBuilder.size(limit); 
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
	    		Map<String,Object> data=new HashMap<String,Object>();
	    		data.put("index",hit.getIndex());
	    		data.put("type",hit.getType());
	    		data.put("id", hit.getId());
	    		 eData.add(data);
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, null);
	}


	public AllSearchResponse documentSearch(String speciesname, String location, String license, String query,
			String name, String contributor, String tag, String content, String attribution, String participants,
			String doctype,Integer from, Integer limit) {
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
		
		sourceBuilder.from(from); 
		sourceBuilder.size(limit); 
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
	    		Map<String,Object> data=new HashMap<String,Object>();
	    		data.put("index",hit.getIndex());
	    		data.put("type",hit.getType());
	    		data.put("id", hit.getId());
	    		 eData.add(data);
	    	}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return new AllSearchResponse(hits.getTotalHits(), eData, null);
	}

}
