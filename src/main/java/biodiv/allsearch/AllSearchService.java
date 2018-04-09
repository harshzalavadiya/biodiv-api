package biodiv.allsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.Filters.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class AllSearchService {

	@Inject
	private RestHighLevelClient client;

	private static final String observation="observation";
	private static final String species="species";
	private static final String document="document";
	private static final String newsletter="newsletter";
	private static final String suser="suser";
	private static final String resource="resource";
	private static final String usergroup="usergroup";
	
	public AllSearchResponse search(List<String> objectType,String speciesname, String location,
			String license,String query,String name, String contributor, String tag,
			String content, String attribution, String participants,String title,
			String pages, String doctype,String user, Integer from,Integer limit ) {
		// TODO Auto-generated method stub
		
		/**
		 * Create indices for searching
		 */
		String[] stockArr = new String[objectType.size()];
		stockArr = objectType.toArray(stockArr);
		SearchRequest searchRequest = new SearchRequest(stockArr); 
		
		/**
		 * Provide bossting for certain indices
		 */
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().indexBoost(suser,8).indexBoost(species, 7).indexBoost(observation, 6).indexBoost(usergroup, 5).
				indexBoost(resource,4).indexBoost(newsletter, 3).indexBoost(document, 2); 
		
		//SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			
		
		/**
		 * 
		 * Building bool query
		 * Masterboolquery is for ANDing all the result
		 * boolquery is for ORing all result
		 */
		BoolQueryBuilder boolQuery = null;
		
		BoolQueryBuilder masterBoolQuery = QueryBuilders.boolQuery();
		
		if(location!=null && !location.isEmpty()){
			
			boolQuery=QueryBuilders.boolQuery();
			
			boolQuery.should(QueryBuilders.matchQuery("placename", location));
			masterBoolQuery.must(boolQuery);
		}
		if(license!=null && !license.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("license.firstValue", license));
			boolQuery.should(QueryBuilders.matchQuery("license", license));
			boolQuery.should(QueryBuilders.matchQuery("licensename", license));
			masterBoolQuery.must(boolQuery);
		}
		if(query!=null && !query.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("all", query));
			masterBoolQuery.must(boolQuery);
		}
		if(name!=null && !name.isEmpty()){
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
		
		if(contributor!=null && !contributor.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("authorname", contributor));
			boolQuery.should(QueryBuilders.matchQuery("contributor.value", contributor));
			boolQuery.should(QueryBuilders.matchQuery("username", contributor));
			boolQuery.should(QueryBuilders.matchQuery("member", contributor));
			masterBoolQuery.must(boolQuery);
		}
		if(tag!=null && !tag.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("tag", tag));
			masterBoolQuery.must(boolQuery);
		}
		if(attribution!=null && !attribution.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("attribution", attribution));
			boolQuery.should(QueryBuilders.matchQuery("group_webaddress", name));
			masterBoolQuery.must(boolQuery);
		}
		if(participants!=null && !participants.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("participants", participants));
			boolQuery.should(QueryBuilders.matchQuery("member", participants));
			masterBoolQuery.must(boolQuery);
		}

		if(title!=null && !title.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("title", title));
			 masterBoolQuery.must(boolQuery);
		}
		if(pages!=null && !pages.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("pages", pages));
			masterBoolQuery.must(boolQuery);
		}
		if(doctype!=null && !doctype.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("doc_type", doctype));
			 masterBoolQuery.must(boolQuery);
		}
		if(user!=null && !user.isEmpty()){
			boolQuery=QueryBuilders.boolQuery();
			boolQuery.should(QueryBuilders.matchQuery("user", user));
			boolQuery.should(QueryBuilders.matchQuery("username", user));
			boolQuery.should(QueryBuilders.matchQuery("email", user));
			 masterBoolQuery.must(boolQuery);
		}
		/**
		 * Create complete query
		 */
		
		
		sourceBuilder.query(masterBoolQuery); 
		
		/**
		 * Limit offset
		 */
		sourceBuilder.from(from); 
		sourceBuilder.size(limit); 
		
		
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		/**
		 * Building aggregation on type level
		 */
		sourceBuilder
		.aggregation(AggregationBuilders.filters(suser,QueryBuilders.typeQuery(suser)))
		.aggregation(AggregationBuilders.filters(newsletter,QueryBuilders.typeQuery(newsletter)))
		.aggregation(AggregationBuilders.filters(resource,QueryBuilders.typeQuery(resource)))
		.aggregation(AggregationBuilders.filters(document,QueryBuilders.typeQuery(document)))
		.aggregation(AggregationBuilders.filters(usergroup,QueryBuilders.typeQuery(usergroup)))
		.aggregation(AggregationBuilders.filters(observation,QueryBuilders.typeQuery(observation)))
		.aggregation(AggregationBuilders.filters(species,QueryBuilders.typeQuery(species)));
		searchRequest.source(sourceBuilder);
		
		/**
		 *Getting the result hits 
		 */
		List<Map<String,Object>> eData=new ArrayList<Map<String, Object>>();
		SearchHits hits=null;
		Aggregations aggregations=null;
		
		List<Map<String,Object>> agg=new ArrayList<Map<String,Object>>();
		
		try {
			
			SearchResponse searchResponse = client.search(searchRequest);
			 hits = searchResponse.getHits();
			  aggregations= searchResponse.getAggregations();
			  
			  Map<String, org.elasticsearch.search.aggregations.Aggregation> aggregationMap = aggregations.getAsMap();
			  Filters observationsf = (Filters) aggregationMap.get(observation);
			  Filters speciesf = (Filters) aggregationMap.get(species);
			  Filters documentsf = (Filters) aggregationMap.get(document);
			  Filters newsletterf = (Filters) aggregationMap.get(newsletter);
			  Filters userf = (Filters) aggregationMap.get(suser);
			  Filters resourcef = (Filters) aggregationMap.get(resource);
		
			  Filters usergroupf = (Filters) aggregationMap.get(usergroup);
			
			  
			  
			  Bucket observationsB = observationsf.getBucketByKey("0");
			  Bucket speciesB = speciesf.getBucketByKey("0");
			  Bucket documentsB = documentsf.getBucketByKey("0");
			  Bucket newsletterB = newsletterf.getBucketByKey("0");
			  Bucket userB = userf.getBucketByKey("0");
			  Bucket resourceB = resourcef.getBucketByKey("0");
			  Bucket usergroupB = usergroupf.getBucketByKey("0");
			  
			  
			  if(observationsB.getDocCount()>0){
				  Map<String, Object> obv=new HashMap<String,Object>();
				  obv.put("Observation", observationsB.getDocCount());  
				  agg.add(obv);
				  
			  }
			  
			 
			  if(speciesB.getDocCount()>0){
				  Map<String, Object> spe=new HashMap<String,Object>();
				  spe.put("Species", speciesB.getDocCount());
				  agg.add(spe);
				  
			  }
			  
			  
			 
			  if(documentsB.getDocCount()>0){
				  Map<String, Object> doc=new HashMap<String,Object>();
				  doc.put("Documnets", documentsB.getDocCount());
				  agg.add(doc);
			  }
			 
			  
			  
			  if(newsletterB.getDocCount()>0){
				  Map<String, Object> news=new HashMap<String,Object>();
				  news.put("NewsLetter", newsletterB.getDocCount());
				  agg.add(news);
			  }
			  
			  
			  
			  if(userB.getDocCount()>0){
				  Map<String, Object> use=new HashMap<String,Object>();
				  use.put("SUser", userB.getDocCount());
				  agg.add(use);
			  }
			  
			  
			 
			  if(resourceB.getDocCount()>0){
				  Map<String, Object> res=new HashMap<String,Object>();
				  res.put("Resource", resourceB.getDocCount());
				  agg.add(res);
			  }
			  
			
			 
			  if(usergroupB.getDocCount()>0){
				  Map<String, Object> userg=new HashMap<String,Object>();
				  userg.put("UserGroup", usergroupB.getDocCount());
				  agg.add(userg);
			  }
			  
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

	public AllSearchResponse obvsearch(List<String> objectType, String speciesname, String location, String license,
			String query, String name, String contributor, String tag, String content, String attribution,
			String participants, String title, String pages, String doctype, Integer from, Integer limit) {
		// TODO Auto-generated method stub
		
				/**
				 * Create indices for searching
				 */
				String[] stockArr = new String[objectType.size()];
				stockArr = objectType.toArray(stockArr);
				SearchRequest searchRequest = new SearchRequest(stockArr); 
				
				/**
				 * Provide bossting for certain indices
				 */
				SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().indexBoost(suser,8).indexBoost(species, 7).indexBoost(observation, 6).indexBoost(usergroup, 5).
						indexBoost(resource,4).indexBoost(newsletter, 3).indexBoost(document, 2); 
				
				/**
				 * Building bool query
				 * Masterboolquery is for ANDing all the result
				 * boolquery is for ORing all result
				 */
				BoolQueryBuilder boolQuery = null;
				BoolQueryBuilder masterBoolQuery = QueryBuilders.boolQuery();
				
				if(location!=null && !location.isEmpty()){
					boolQuery=QueryBuilders.boolQuery();
					boolQuery.should(QueryBuilders.matchQuery("placename", location));
					masterBoolQuery.must(boolQuery);
				}
				if(license!=null && !license.isEmpty()){
					boolQuery=QueryBuilders.boolQuery();
					boolQuery.should(QueryBuilders.matchQuery("licensename", license));
					masterBoolQuery.must(boolQuery);
				}
				if(query!=null && !query.isEmpty()){
					boolQuery=QueryBuilders.boolQuery();
					boolQuery.should(QueryBuilders.matchQuery("all", query));
					masterBoolQuery.must(boolQuery);
				}
				if(name!=null && !name.isEmpty()){
					boolQuery=QueryBuilders.boolQuery();
					boolQuery.should(QueryBuilders.matchQuery("name", name));
					masterBoolQuery.must(boolQuery);
				}
				
				if(contributor!=null && !contributor.isEmpty()){
					boolQuery=QueryBuilders.boolQuery();
					boolQuery.should(QueryBuilders.matchQuery("authorname", contributor));
					masterBoolQuery.must(boolQuery);
				}
		
				
				

				if(title!=null && !title.isEmpty()){
					boolQuery=QueryBuilders.boolQuery();
					boolQuery.should(QueryBuilders.matchQuery("title", title));
					 masterBoolQuery.must(boolQuery);
				}
				
				/**
				 * Create complete query
				 */
				
				
				sourceBuilder.query(masterBoolQuery); 
				
				/**
				 * Limit offset
				 */
				sourceBuilder.from(from); 
				sourceBuilder.size(limit); 
				
				
				sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
				/**
				 * Building aggregation on type level
				 */
				sourceBuilder
				.aggregation(AggregationBuilders.filters(suser,QueryBuilders.typeQuery(suser)))
				.aggregation(AggregationBuilders.filters(newsletter,QueryBuilders.typeQuery(newsletter)))
				.aggregation(AggregationBuilders.filters(resource,QueryBuilders.typeQuery(resource)))
				.aggregation(AggregationBuilders.filters(document,QueryBuilders.typeQuery(document)))
				.aggregation(AggregationBuilders.filters(usergroup,QueryBuilders.typeQuery(usergroup)))
				.aggregation(AggregationBuilders.filters(observation,QueryBuilders.typeQuery(observation)))
				.aggregation(AggregationBuilders.filters(species,QueryBuilders.typeQuery(species)));
				searchRequest.source(sourceBuilder);
				
				/**
				 *Getting the result hits 
				 */
				List<Map<String,Object>> eData=new ArrayList<Map<String, Object>>();
				SearchHits hits=null;
				Aggregations aggregations=null;
				
				List<Map<String,Object>> agg=new ArrayList<Map<String,Object>>();
				
				try {
					
					SearchResponse searchResponse = client.search(searchRequest);
					 hits = searchResponse.getHits();
					  aggregations= searchResponse.getAggregations();
					  
					  Map<String, org.elasticsearch.search.aggregations.Aggregation> aggregationMap = aggregations.getAsMap();
					  Filters observationsf = (Filters) aggregationMap.get(observation);
					  Filters speciesf = (Filters) aggregationMap.get(species);
					  Filters documentsf = (Filters) aggregationMap.get(document);
					  Filters newsletterf = (Filters) aggregationMap.get(newsletter);
					  Filters userf = (Filters) aggregationMap.get(suser);
					  Filters resourcef = (Filters) aggregationMap.get(resource);
				
					  Filters usergroupf = (Filters) aggregationMap.get(usergroup);
					
					  
					  
					  Bucket observationsB = observationsf.getBucketByKey("0");
					  Bucket speciesB = speciesf.getBucketByKey("0");
					  Bucket documentsB = documentsf.getBucketByKey("0");
					  Bucket newsletterB = newsletterf.getBucketByKey("0");
					  Bucket userB = userf.getBucketByKey("0");
					  Bucket resourceB = resourcef.getBucketByKey("0");
					  Bucket usergroupB = usergroupf.getBucketByKey("0");
					  
					  
					  if(observationsB.getDocCount()>0){
						  Map<String, Object> obv=new HashMap<String,Object>();
						  obv.put("Observation", observationsB.getDocCount());  
						  agg.add(obv);
						  
					  }
					  
					 
					  if(speciesB.getDocCount()>0){
						  Map<String, Object> spe=new HashMap<String,Object>();
						  spe.put("Species", speciesB.getDocCount());
						  agg.add(spe);
						  
					  }
					  
					  
					 
					  if(documentsB.getDocCount()>0){
						  Map<String, Object> doc=new HashMap<String,Object>();
						  doc.put("Documnets", documentsB.getDocCount());
						  agg.add(doc);
					  }
					 
					  
					  
					  if(newsletterB.getDocCount()>0){
						  Map<String, Object> news=new HashMap<String,Object>();
						  news.put("NewsLetter", newsletterB.getDocCount());
						  agg.add(news);
					  }
					  
					  
					  
					  if(userB.getDocCount()>0){
						  Map<String, Object> use=new HashMap<String,Object>();
						  use.put("SUser", userB.getDocCount());
						  agg.add(use);
					  }
					  
					  
					 
					  if(resourceB.getDocCount()>0){
						  Map<String, Object> res=new HashMap<String,Object>();
						  res.put("Resource", resourceB.getDocCount());
						  agg.add(res);
					  }
					  
					
					 
					  if(usergroupB.getDocCount()>0){
						  Map<String, Object> userg=new HashMap<String,Object>();
						  userg.put("UserGroup", usergroupB.getDocCount());
						  agg.add(userg);
					  }
					  
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

}
