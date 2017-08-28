package biodiv;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;

public class CheckRequestFilter implements ClientRequestFilter {

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub
		if (requestContext.getHeaders().get("App-Key") == null) {
			requestContext.abortWith(
					Response.status(Response.Status.BAD_REQUEST).entity("Client-Name header must be defined.").build());
		}
	}

}
