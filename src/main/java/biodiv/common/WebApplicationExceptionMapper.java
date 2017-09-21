package biodiv.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import javax.ws.rs.WebApplicationException;
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException exception) {
		exception.printStackTrace();
		return exception.getResponse();
            /*.entity("WebApplicationException !! : " + exception.getMessage())
				.type("text/plain").build();
                */
	}

}
