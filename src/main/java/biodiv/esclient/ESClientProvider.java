
package biodiv.esclient;

import javax.inject.Singleton;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;



/**
 * Provides an elastic search client
 * 
 *
 */
@Singleton
public class ESClientProvider {

	private static ElasticSearchClient client;

	private ESClientProvider() {
	}

	private static synchronized void initClient() {
		client = new ElasticSearchClient(RestClient.builder(HttpHost.create("localhost:9200")));
	}

	public static ElasticSearchClient getClient() {

		if (client == null)
			initClient();

		return client;
	}

	public static RestClient getLowLevelClient() {
		if (client == null)
			initClient();

		return client.getLowLevelClient();
	}

}
