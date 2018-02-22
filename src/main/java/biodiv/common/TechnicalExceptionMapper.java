package biodiv.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.pac4j.core.exception.TechnicalException;

import biodiv.auth.AccountNotFoundException;

@Provider
@Singleton
public class TechnicalExceptionMapper implements ExceptionMapper<TechnicalException> {

	@Override
	public Response toResponse(TechnicalException exception) {
		exception.printStackTrace();

		Response response = null;
		if (exception.getCause() instanceof AccountNotFoundException) {
			UriBuilder uriBuilder;
			try {
				Map<String, String> details = ((AccountNotFoundException)exception.getCause()).getDetails();
				uriBuilder = UriBuilder.fromUri(new URI(details.get("redirect_url")));
		        details.remove("redirect_uri");
				for (Map.Entry<String,String> entry : details.entrySet()) { 
		        	uriBuilder.queryParam(entry.getKey(), entry.getValue());
		        }
		        
				URI targetURIForRedirection = uriBuilder.build();
				return Response.temporaryRedirect(targetURIForRedirection).build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			response = Response.status(500)
					.entity("TechnicalException Please try again !! Exception : " + exception.getMessage())
					.type("text/plain").build();
		}
		return response;
	}

}
