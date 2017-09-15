package biodiv.auth;

import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pac4j.jax.rs.annotations.Pac4JLogout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.token.TokenService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;

@Path("/logout")
public class LogoutController {

	private final Logger log = LoggerFactory.getLogger(LogoutController.class);
	private TokenService tokenService = new TokenService();
	private UserService userService = new UserService();

	@DELETE
	@Pac4JLogout
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@HeaderParam("X-AUTH-TOKEN") String accessToken) {
		if (accessToken != null) {
			try {
				CustomJwtAuthenticator jwtAuthenticator = new CustomJwtAuthenticator(
						new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT));

				String userId = jwtAuthenticator.getProfileId(accessToken);
				User user = userService.findById(Long.parseLong(userId));

				tokenService.removeRefreshToken(user);

				ResponseModel responseModel = new ResponseModel(Response.Status.OK, "Successfully logged out...");
				return Response.status(Response.Status.OK).entity(responseModel).build();
			} catch (Exception e) {
				e.printStackTrace();
				ResponseModel responseModel = new ResponseModel(Response.Status.BAD_REQUEST, e.getMessage());
				return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
			}
		} else {
			ResponseModel responseModel = new ResponseModel(Response.Status.BAD_REQUEST,
					"Access token is required to identify the user");
			return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
		}
	}

}
