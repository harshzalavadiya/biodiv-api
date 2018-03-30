package biodiv;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class BiodivResponseFilter implements ContainerResponseFilter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
	public final static int MAX_AGE = 42 * 60 * 60;
	public final static String DEFAULT_ALLOWED_HEADERS = "origin, content-type, accept, authorization, X-Requested-With, X-Auth-Token, X-AppKey";
	public final static String DEFAULT_EXPOSED_HEADERS = "location,info";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("X-Powered-By", "Biodiv App");

		URI baseUri = requestContext.getUriInfo().getBaseUri();
		String origin = requestContext.getHeaderString("Origin");
		if(!isValidOrigin(origin)) {
			origin = "*";
		}
		headers.add("Access-Control-Allow-Origin", origin);
		headers.add("Access-Control-Allow-Headers", getRequestedAllowedHeaders(requestContext));
		headers.add("Access-Control-Expose-Headers", getRequestedExposedHeaders(requestContext));
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
		headers.add("Access-Control-Max-Age", MAX_AGE);
	}

	private String getRequestedAllowedHeaders(ContainerRequestContext responseContext) {
		List<String> headers = responseContext.getHeaders().get("Access-Control-Allow-Headers");
		return createHeaderList(headers, DEFAULT_ALLOWED_HEADERS);
	}

	private String getRequestedExposedHeaders(ContainerRequestContext responseContext) {
		List<String> headers = responseContext.getHeaders().get("Access-Control-Expose-Headers");
		return createHeaderList(headers, DEFAULT_EXPOSED_HEADERS);
	}

	private String createHeaderList(List<String> headers, String defaultHeaders) {
		if (headers == null || headers.isEmpty()) {
			return defaultHeaders;
		}
		StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < headers.size(); i++) {
			String header = (String) headers.get(i);
			retVal.append(header);
			retVal.append(',');
		}
		retVal.append(defaultHeaders);
		return retVal.toString();
	}

	private boolean isValidOrigin(String origin) {
		// TODO : ideally baseUri should be matched against given set of urls in
		// config
		return StringUtils.contains(origin, "indiabiodiversity.org") ? true : false;
	}
}
