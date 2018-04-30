package biodiv.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UncaughtException extends Throwable implements ExceptionMapper<Throwable> {

	private static final long serialVersionUID = 1L;

	@Override
	public Response toResponse(Throwable exception) {
		exception.printStackTrace();
		String msg = getMessage(exception);
		return Response.status(500)
				.entity("Something bad happened. Please try again !! Exception : " + msg)
				.type("text/plain").build();
	}
	
	private String getMessage(Throwable exception) {
		String msg = exception.getMessage();
		while (exception.getCause() != null) {
			msg = getMessage(exception.getCause())+" --- "+msg;
		}
		return msg;
		
	}

}
