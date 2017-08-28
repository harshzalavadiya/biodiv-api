package biodiv.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.ResponseModel;

@Path("/login")
public class LoginController {

	private final Logger log = LoggerFactory.getLogger(LoginController.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response auth(@QueryParam("username") String username, @QueryParam("password") String password) {

		try {
			//validate credentials
			CommonProfile profile = authenticate(username, password);

			// Issue a token for the user
			String token = issueToken(profile);

			// Return the token on the response
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("token", token);
			return Response.ok(result).build();

		} catch (Exception e) {
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		}      
	}

	private CommonProfile authenticate(String username, String password) throws Exception {
		SimpleUsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleUsernamePasswordAuthenticator();
		// Authenticate the user using the credentials provided
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, "");
		usernamePasswordAuthenticator.validate(credentials, null);
		return credentials.getUserProfile();
	}

	private String issueToken(CommonProfile profile) {
		JwtGenerator<CommonProfile> generator = new JwtGenerator<>(
				new SecretSignatureConfiguration(Constants.JWT_SALT));
		String jwtToken = generator.generate(profile);
		return jwtToken;
	}

	@Path("/callback")
	@GET
    @Pac4JCallback(skipResponse = true)
	@Produces(MediaType.APPLICATION_JSON)
    public Response callback(@Pac4JProfile Optional<CommonProfile> profile) {
		try {
			if (profile.isPresent()) {
				// Issue a token for the user
				String token = issueToken(profile.get());

				// Return the token on the response
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("token", token);
				return Response.ok(result).build();
			} else {
				throw new CredentialsException("Invalid credentials");
			}
		}catch (Exception e) {
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		} 
    }
	
	
}
