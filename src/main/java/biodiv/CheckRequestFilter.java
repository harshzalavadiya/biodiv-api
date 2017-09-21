package biodiv;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class CheckRequestFilter implements ClientRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(CheckRequestFilter.class);

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		if (requestContext.getHeaders().get("App-Key") == null) {
			requestContext.abortWith(
					Response.status(Response.Status.BAD_REQUEST).entity("Client-Name header must be defined.").build());
		}
	}
}
