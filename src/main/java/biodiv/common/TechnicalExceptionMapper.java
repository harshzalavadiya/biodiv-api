package biodiv.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.pac4j.core.exception.TechnicalException;

@Provider
public class TechnicalExceptionMapper implements ExceptionMapper<TechnicalException> {

	@Override
	public Response toResponse(TechnicalException exception) {
		exception.printStackTrace();
		return Response.status(500)
				.entity("TechnicalException Please try again !! Exception : " + exception.getMessage())
				.type("text/plain").build();
	}

}
