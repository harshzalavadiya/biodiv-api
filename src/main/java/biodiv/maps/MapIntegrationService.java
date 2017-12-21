package biodiv.maps;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is used for communication with the map module
 *
 */
public class MapIntegrationService {

	private final Logger logger = LoggerFactory.getLogger(MapIntegrationService.class);

	/**
	 * The context helps maintain a session across http requests.
	 */
	private HttpClientContext context;

	/**
	 * The connection manager for this session. Maintains a pool of connections for
	 * the session.
	 */
	private PoolingHttpClientConnectionManager manager;

	/**
	 * The maximum number of connections to maintain per route by the pooling client
	 * manager
	 */
	private final int MAX_CONNECTIONS_PER_ROUTE = 5;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public MapIntegrationService() {
		initHttpConnection();
	}

	private void initHttpConnection() {
		manager = new PoolingHttpClientConnectionManager();
		manager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

		CookieStore cookieStore = new BasicCookieStore();
		context = HttpClientContext.create();
		context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}

	/**
	 * Post request to given uri
	 * 
	 * @param uri
	 * @param data
	 */
	public MapHttpResponse postRequest(String uri, Object data) {
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpPost post = new HttpPost(uri);

			String jsonData = objectMapper.writeValueAsString(data);
			StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);

			post.setEntity(entity);

			try {
				response = httpclient.execute(post, context);
				return getMapHttpResponse(response);
			} catch (IOException e) {
				logger.error("Error while trying to send request at URL {}", uri);
			} finally {
				if (response != null)
					HttpClientUtils.closeQuietly(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while trying to send request at URL {}", uri);
		}
		
		return null;
	}

	private MapHttpResponse getMapHttpResponse(CloseableHttpResponse response) throws ParseException, IOException {

		String message = null;

		if (response != null) {
			HttpEntity httpEntity = response.getEntity();
			if (httpEntity != null) {
				message = EntityUtils.toString(httpEntity);
			}
		}

		return new MapHttpResponse(response.getStatusLine().getStatusCode(), message);
	}

}