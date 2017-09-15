package biodiv.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jwt.profile.JwtProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import biodiv.auth.token.TokenService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;

@Path("/login")
public class LoginController {

	private final Logger log = LoggerFactory.getLogger(LoginController.class);
	private TokenService tokenService = new TokenService();
	private UserService userService = new UserService();

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response auth(@FormParam("username") String username, @FormParam("password") String password) {

		try {
			// validate credentials
			CommonProfile profile = authenticate(username, password);

			// Issue a token for the user
			Map result = tokenService.buildTokenResponse(profile, true);

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

	private CommonProfile authenticate(String username, String password) throws Exception {
		SimpleUsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleUsernamePasswordAuthenticator();
		// Authenticate the user using the credentials provided
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, "");
		usernamePasswordAuthenticator.validate(credentials, null);
		return credentials.getUserProfile();
	}

	@Path("/callback")
	@GET
	@Pac4JCallback(skipResponse = true)
	@Produces(MediaType.APPLICATION_JSON)
	public Response callback(@Pac4JProfile Optional<CommonProfile> profile) {
		try {
			if (profile.isPresent()) {
				
				// Issue a token for the user
				Map result = tokenService.buildTokenResponse(profile.get(), true);
				
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

	@Path("/accessToken")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response accessToken(@QueryParam("refresh_token") String refreshToken) {
		try {
			/*if (accessToken == null) {
				ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "Invalid access token");
				return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			}*/
			if (refreshToken == null) {
				ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "Invalid refresh token");
				return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			}
			//System.out.println(accessToken);
			CustomJwtAuthenticator jwtAuthenticator = new CustomJwtAuthenticator(
					new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT));

            Token refreshTokenInstance = tokenService.findByValue(refreshToken);
			//User user = tokenService.findUser(refreshToken)//jwtAuthenticator.getProfileId(accessToken);
			//User user = userService.findById(Long.parseLong(userId));			
			//CommonProfile profile = AuthUtils.createUserProfile(user);

			// get user details from access token and validate if the refresh
			// token was given to this user.
			if (refreshTokenInstance && tokenService.isValidRefreshToken(refreshToken, refreshToken.user.getId())) {
				Map result = tokenService.buildTokenResponse(profile, false);
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

	@Path("/refreshToken")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient", authorizers = "isAuthenticated")
	public Response refreshToken(@QueryParam("refresh_token") String refreshToken,
			@Pac4JProfile CommonProfile profile) {
		try {
			// check if refresh token is valid and indeed it was given to this
			// user
			if (tokenService.isValidRefreshToken(refreshToken, Long.parseLong(profile.getId()))) {
				Map result = tokenService.buildTokenResponse(profile, true);
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
