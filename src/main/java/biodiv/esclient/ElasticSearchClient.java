package biodiv.esclient;


import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClient extends RestHighLevelClient {

	public ElasticSearchClient(RestClientBuilder restClientBuilder) {
		super(restClientBuilder);
	}

}
