package biodiv;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class PreMatchingFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub
		// change all PUT methods to POST
        if (requestContext.getMethod().equals("PUT")) {
            requestContext.setMethod("POST");
        }
	}

}
