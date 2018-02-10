package biodiv.esclient;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClient  {
	RestHighLevelClient client = new RestHighLevelClient(
	        RestClient.builder(
	                new HttpHost("localhost", 9201, "http")));
	
}
