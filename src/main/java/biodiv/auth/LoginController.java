package biodiv.auth;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.configuration2.Configuration;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.auth.token.Token;
import biodiv.auth.token.TokenService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;
import javax.ws.rs.NotFoundException;

import javax.inject.Inject;

@Path("/login")
public class LoginController {

	private final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Inject
	private TokenService tokenService;

	@Inject
	private UserService userService;

	@Inject
	private SimpleUsernamePasswordAuthenticator usernamePasswordAuthenticator;

	@Inject
	Configuration config;

	public LoginController() {
		log.debug("Login Controller");
	}

	/**
	 * 
	 * @param username
	 *            user name
	 * @param password
	 *            password
	 * @return returns something
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
    @Path("/auth")
	public Response auth(@FormParam("username") String username, @FormParam("password") String password) {

		try {
			// validate credentials
			CommonProfile profile = authenticate(username, password);

			// Issue a token for the user

			// Map<String, Object> result =
			// tokenService.buildTokenResponse(profile, true);

			// Create a proxy object for logged in user

			User user = userService.findById(Long.parseLong(profile.getId()));
			Map<String, Object> result = tokenService.buildTokenResponse(profile, user, true);

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
	 *            username
	 * @param password
	 *            password
	 * @return is valid or not
	 * @throws Exception
	 *             Possible error
	 */
	private CommonProfile authenticate(String username, String password) throws Exception {
		// Authenticate the user using the credentials provided
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password, "");
		usernamePasswordAuthenticator.validate(credentials, null);
		return credentials.getUserProfile();
	}

	/**
	 * 
	 * @param profile
	 *            profile
	 * @return profile
	 */
	@Path("/callback")
	@GET
	@Pac4JCallback(skipResponse = true)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response callback(@Pac4JProfile Optional<CommonProfile> profile) {
		try {
			if (profile.isPresent()) {

				// Issue a token for the user
				User user = null;
				try {
					user = userService.findByEmail(profile.get().getEmail());
				} catch (NotFoundException e) {
					log.error("Could not find a user with email : {}", profile.get().getEmail());
					log.error("Trying to register...");
				}
				if (user != null) {
					Map<String, Object> result = tokenService.buildTokenResponse(profile.get(), user, true);

					log.debug(result.toString());
					UriBuilder targetURIForRedirection = UriBuilder.fromPath(config.getString("checkAuthUrl"));
					Iterator it = result.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
                        if(pair.getValue() != null) {
						    targetURIForRedirection.queryParam((String) pair.getKey(), pair.getValue());
                        }
						it.remove(); // avoids a ConcurrentModificationException
					}
					return Response.temporaryRedirect(targetURIForRedirection.build()).build();
				} else {
					UriBuilder uriBuilder = UriBuilder
							.fromUri(new URI(config.getString("createSocialAccountFromProfile")));				
					URI targetURIForRedirection = uriBuilder.build();
					return Response.temporaryRedirect(targetURIForRedirection).build();
				}
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
	 *            dummy
	 * @param refreshToken
	 *            dummy
	 * @return dummy
	 */
	@Path("/token")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response token(@FormParam("grant_type") String grantType, @FormParam("refresh_token") String refreshToken) {
		try {
			log.debug("Getting new " + grantType);
			if (refreshToken == null) {
				ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "Invalid refresh token");
				return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			}
			if (grantType == null) {
				grantType = Token.TokenType.ACCESS.value();
			}

			log.debug("Auth Request : Refresh Token : " + refreshToken + "  grant_type : " + grantType);

			// get user details from access token and validate if the refresh
			// token was given to this user.
			if (refreshToken != null) {
				CommonProfile profile = tokenService.createUserProfile(refreshToken);
				User user = userService.findById(Long.parseLong(profile.getId()));
				if (tokenService.isValidRefreshToken(refreshToken, user.getId())) {
					Map<String, Object> result = tokenService.buildTokenResponse(profile, user,
							grantType.equalsIgnoreCase(Token.TokenType.REFRESH.value()) ? true : false);
					result.put(Token.TokenType.REFRESH.value(), refreshToken);
					return Response.ok(result).build();
				} else {
					ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "Invalid refresh token");
					return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
				}
			} else {
				ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, "No refresh token");
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
