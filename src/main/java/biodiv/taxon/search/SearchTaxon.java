package biodiv.taxon.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import biodiv.taxon.service.TaxonService;

public class SearchTaxon {
	
	TaxonService taxonService = new TaxonService();

	
	private void publishSearchIndex(TransportClient client) {
		// TODO Auto-generated method stub
		
		Integer offset = 0;
		Integer limit = 10000;
		Boolean stop = true;
		int count = 0;
		while (stop) {
			count++;
			List<Object[]> results = new ArrayList<Object[]>();
			results.clear();
			results = taxonService.getTaxonData(offset, limit);


			if (results.size() == 0) {
				stop = false;
			} else {
				offset = offset + limit + 1;
			}
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
			for (Object[] result : results) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("name", result[0]);
				m.put("id", result[1]);
				m.put("status", result[2]);
				m.put("position", result[3]);
				m.put("rank", result[4]);
				String id= String.valueOf(result[1]);
			
				bulkRequest.add(client.prepareIndex("biodiv", "taxon",id).setSource(m));
			}
			BulkResponse bulkResponse = bulkRequest.get();

		}

	}

	public List<Map<String, Object>> search(String data) {
		
		Boolean call=false;
		
		Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
		TransportClient client = new PreBuiltTransportClient(settings);
		try {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		if(call){
			publishSearchIndex(client);
		}
		//publishSearchIndex(client);
		
		SearchResponse result = client.prepareSearch("biodiv").setTypes("taxon")
				.setQuery(QueryBuilders.matchQuery("name", data))
				.execute().actionGet();
		List<Map<String, Object>> esData = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : result.getHits()) {
			esData.add(hit.getSource());
		}
		client.close();
		return esData;
	}


}