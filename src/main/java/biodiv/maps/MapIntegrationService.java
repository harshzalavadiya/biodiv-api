package biodiv.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

import biodiv.observation.ObservationListMapper;

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
	 * The connection manager for this session. Maintains a pool of connections
	 * for the session.
	 */
	private PoolingHttpClientConnectionManager manager;

	/**
	 * The maximum number of connections to maintain per route by the pooling
	 * client manager
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
	 * Post request to given url
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

	public MapHttpResponse getRequest(String url) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		CloseableHttpResponse response1 = null;
		try {
			response1 = httpClient.execute(httpGet);
			HttpEntity entity1 = response1.getEntity();
			String content = EntityUtils.toString(entity1);
			ObjectMapper mapper = new ObjectMapper();
			MapDocument mapDocument = mapper.readValue(content, MapDocument.class);
				
			ObservationListMapper result = mapper.readValue(String.valueOf(mapDocument.getDocument()),
					ObservationListMapper.class);

			return new MapHttpResponse(response1.getStatusLine(), result);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			return new MapHttpResponse(response1.getStatusLine(), "ClientProtocol Exception");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new MapHttpResponse(response1.getStatusLine(), "IO Exception");
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			e.printStackTrace();
			return new MapHttpResponse(response1.getStatusLine(), "Illegal Argument Exception");
		} catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
			return new MapHttpResponse(response1.getStatusLine(), "Parse Exception");
		}

		finally {
			try {
				response1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return new MapHttpResponse(response1.getStatusLine(), "Some Unknown Exception");
			}
		}

	}

	/**
	 * For observation serach on the basis of the given parameter url consisst
	 * of parmas and path parms List<MapBoolQuer> contain result
	 * 
	 * @param url
	 *            for post request
	 * @param querys
	 *            List
	 * @return the list of document along with there count
	 */
	public MapBiodivResponse postSearch(String url, MapSearchQuery querys) {
		// TODO Auto-generated method stub
		CloseableHttpResponse response = null;
		try {

			HttpPost post = new HttpPost(url);

			String jsonData = objectMapper.writeValueAsString(querys);
			StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);

			post.setEntity(entity);

			try {
				CloseableHttpClient httpclient = HttpClients.createDefault();

				response = httpclient.execute(post, context);
				
				HttpEntity entity1 = response.getEntity();

				String responseString = EntityUtils.toString(entity1);
				
				
				ObjectMapper mapper = new ObjectMapper();

				MapResponse myObject = mapper.readValue(responseString, MapResponse.class);

				List<ObservationListMapper> result = new ArrayList<>();

				for (MapDocument o : myObject.getDocuments()) {
					result.add(mapper.readValue(String.valueOf(o.getDocument()), ObservationListMapper.class));
				}

				return new MapBiodivResponse(result, myObject.getTotalDocuments(), myObject.getGeohashAggregation(), myObject.getViewFilteredGeohashAggregation());

			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Error while trying to send request at URL {}", url);
			} finally {
				if (response != null)
					HttpClientUtils.closeQuietly(response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private MapHttpResponse getMapHttpResponse(CloseableHttpResponse response) throws ParseException, IOException {

		String message = null;

		if (response != null) {
			HttpEntity httpEntity = response.getEntity();

			if (httpEntity != null) {
				message = EntityUtils.toString(httpEntity);
				return new MapHttpResponse(response.getStatusLine(), message);
			}
		}
		
		return null;
	}

	/**
	 * 
	 * Get single observation object on the basis of object id
	 * 
	 * @param url
	 *            url from where you want to fecth data
	 * @return return the single observation object
	 */
	public MapHttpResponse getSingleSearch(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		CloseableHttpResponse response1 = null;
		try {
			response1 = httpClient.execute(httpGet);
			HttpEntity entity1 = response1.getEntity();

			String content = EntityUtils.toString(entity1);
			ObjectMapper mapper = new ObjectMapper();
			Object rootAsClass = mapper.readValue(content, ObservationListMapper.class);

			return new MapHttpResponse(response1.getStatusLine(), rootAsClass);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			return new MapHttpResponse(response1.getStatusLine(), "ClientProtocol Exception");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new MapHttpResponse(response1.getStatusLine(), "IO Exception");
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			e.printStackTrace();
			return new MapHttpResponse(response1.getStatusLine(), "Illegal Argument Exception");
		} catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
			return new MapHttpResponse(response1.getStatusLine(), "Parse Exception");
		}

		finally {
			try {
				response1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return new MapHttpResponse(response1.getStatusLine(), "Some Unknown Exception");
			}
		}

	}

	/**
	 * Method to upload seeting
	 * 
	 * @param url
	 * @param settingsAndMappings
	 * @return MapHTTPRespose, which include result
	 */
	public MapHttpResponse uploadSettingAndMappings(String url, String settingsAndMappings) {
		// TODO Auto-generated method stub
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpPost post = new HttpPost(url);

			String jsonData = objectMapper.writeValueAsString(settingsAndMappings);

			StringEntity entity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);

			post.setEntity(entity);

			try {
				response = httpclient.execute(post, context);
				return getMapHttpResponse(response);
			} catch (IOException e) {
				logger.error("Error while trying to send request at URL {}", url);
			} finally {
				if (response != null)
					HttpClientUtils.closeQuietly(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while trying to send request at URL {}", url);
		}

		return null;
	}

	/**
	 * TO update single document All you need to provide is just document id and
	 * key need to be updated
	 * 
	 * @param newurl
	 * @param document
	 * @return
	 */

	public MapResponse updateSingleDocument(String url, String document) {
		// TODO Auto-generated method stub
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpPut put = new HttpPut(url);			
			StringEntity entity = new StringEntity(document, ContentType.APPLICATION_JSON);
			
			
			put.setEntity(entity);

			try {
				response = httpclient.execute(put, context);
			} catch (IOException e) {
				logger.error("Error while trying to send request at URL {}", url);
			} finally {
				if (response != null)
					HttpClientUtils.closeQuietly(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while trying to send request at URL {}", url);
		}

		return null;
	}

	public MapResponse deleteSingleDocument(String url) {
		// TODO Auto-generated method stub
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();

			 HttpDelete httpDelete = new HttpDelete(url);	
			

			try {
				response = httpclient.execute(httpDelete);
			} catch (IOException e) {
				logger.error("Error while trying to send request at URL {}", url);
			} finally {
				if (response != null)
					HttpClientUtils.closeQuietly(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while trying to send request at URL {}", url);
		}

		return null;
	}

}