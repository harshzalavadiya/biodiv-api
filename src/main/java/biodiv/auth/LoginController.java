package biodiv.auth;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.token.Token;
import biodiv.auth.token.TokenService;
import biodiv.common.ResponseModel;

@Path("/login")
public class LoginController {

	private final Logger log = LoggerFactory.getLogger(LoginController.class);
	private TokenService tokenService = new TokenService();

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response auth(@FormParam("username") String username, @FormParam("password") String password) {

		try {
			// validate credentials
			CommonProfile profile = authenticate(username, password);

			// Issue a token for the user
			Map<String, Object> result = tokenService.buildTokenResponse(profile, true);

			// TODO When responding with an access token, the server must also
			// include the additional Cache-Control: no-store and Pragma:
			// no-cache HTTP headers to ensure clients do not cache this
			// request.

			return Response.ok(result).build();

		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		}
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private CommonProfile authenticate(String username, String password) throws Exception {
		SimpleUsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleUsernamePasswordAuthenticator();
		// Authenticate the user using the credentials provided
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, "");
		usernamePasswordAuthenticator.validate(credentials, null);
		return credentials.getUserProfile();
	}

	/**
	 * 
	 * @param profile
	 * @return
	 */
	@Path("/callback")
	@GET
	@Pac4JCallback(skipResponse = true)
	@Produces(MediaType.APPLICATION_JSON)
	public Response callback(@Pac4JProfile Optional<CommonProfile> profile) {
		try {
			if (profile.isPresent()) {

				// Issue a token for the user
				Map<String, Object> result = tokenService.buildTokenResponse(profile.get(), true);

				return Response.ok(result).build();
			} else {
				throw new CredentialsException("Invalid credentials");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		}
	}

	/**
	 * 
	 * @param grantType
	 * @param refreshToken
	 * @return
	 */
	@Path("/token")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response token(@QueryParam("grant_type") String grantType,
			@QueryParam("refresh_token") String refreshToken) {
		try {
			if (refreshToken == null) {
				ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "Invalid refresh token");
				return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			}
			if (grantType == null) {
				grantType = Token.TokenType.ACCESS.value();
			}

			log.debug("Auth Request : Refresh Token : " + refreshToken + "  grant_type : " + grantType);

			//CustomJwtAuthenticator jwtAuthenticator = new CustomJwtAuthenticator(
			//		new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT));

			Token refreshTokenInstance = tokenService.findByValue(refreshToken);
			// User user =
			// tokenService.findUser(refreshToken)//jwtAuthenticator.getProfileId(accessToken);
			// User user = userService.findById(Long.parseLong(userId));
			CommonProfile profile = AuthUtils.createUserProfile(refreshTokenInstance.getUser());

			// get user details from access token and validate if the refresh
			// token was given to this user.
			if (refreshTokenInstance != null
					&& tokenService.isValidRefreshToken(refreshToken, refreshTokenInstance.getUser().getId())) {
				Map<String, Object> result = tokenService.buildTokenResponse(profile,
						grantType.equalsIgnoreCase(Token.TokenType.REFRESH.value()) ? true : false);
				return Response.ok(result).build();
			} else {
				ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "Invalid refresh token");
				return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			}
			// generate a new access token and send a response.
		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		}
	}
}
