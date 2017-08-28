package biodiv.auth;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.pac4j.core.exception.CredentialsException;

import biodiv.common.ResponseModel;

@Provider
public class CredentialsExceptionMapper implements ExceptionMapper<CredentialsException> {

	@Override
	public Response toResponse(CredentialsException exception) {
		exception.printStackTrace();
		return Response.status(Status.UNAUTHORIZED)
				 .entity(new ResponseModel("401", exception.getMessage()))
				 .build();
	}

}
