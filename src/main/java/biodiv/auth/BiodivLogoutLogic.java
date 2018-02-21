package biodiv.auth;

import java.security.Principal;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.token.TokenService;
import biodiv.user.User;
import biodiv.user.UserService;

public class BiodivLogoutLogic<R, C extends WebContext> extends DefaultLogoutLogic<R, C> {

	protected Logger log = LoggerFactory.getLogger(getClass());

	 @Inject
		private TokenService tokenService;

	    @Inject
		private UserService userService;
		
	@Override
	public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
			final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputLocalLogout,
			final Boolean inputDestroySession, final Boolean inputCentralLogout) {
		
		log.debug("=== LOGOUT ===");

		// default values
		final String logoutUrlPattern;
		if (inputLogoutUrlPattern == null) {
			logoutUrlPattern = Pac4jConstants.DEFAULT_LOGOUT_URL_PATTERN_VALUE;
		} else {
			logoutUrlPattern = inputLogoutUrlPattern;
		}
		final boolean localLogout;
		if (inputLocalLogout == null) {
			localLogout = true;
		} else {
			localLogout = inputLocalLogout;
		}
		final boolean destroySession;
		if (inputDestroySession == null) {
			destroySession = false;
		} else {
			destroySession = inputDestroySession;
		}

		// checks
/*		assertNotNull("context", context);
		assertNotNull("config", config);
		assertNotNull("httpActionAdapter", httpActionAdapter);
		assertNotBlank(Pac4jConstants.LOGOUT_URL_PATTERN, logoutUrlPattern);
*/		final Clients configClients = config.getClients();
//		assertNotNull("configClients", configClients);

		final ProfileManager<CommonProfile> manager = getProfileManager(context, config);
		
		log.debug("Profile Manager: {} ",manager);
		
		// compute redirection URL
		final String url = context.getRequestParameter(Pac4jConstants.URL);
		String redirectUrl = defaultUrl;
		if (url != null && Pattern.matches(logoutUrlPattern, url)) {
			redirectUrl = url;
		}
		log.debug("redirectUrl: {}", redirectUrl);
		HttpAction action;
		if (redirectUrl != null) {
			action = HttpAction.redirect("redirect", context, redirectUrl);
		} else {
			action = HttpAction.ok("ok", context);
		}

		// local logout if requested or multiple profiles
		if (localLogout) {
			log.debug("Performing application logout");
			//manager..logout();
			//HACK actually shd call manager.logout()
			logout(context);
			if (destroySession) {
				final SessionStore<WebContext> sessionStore = context.getSessionStore();
				if (sessionStore != null) {
					final boolean removed = sessionStore.destroySession(context);
					if (!removed) {
						log.error(
								"Unable to destroy the web session. The session store may not support this feature");
					}
				} else {
					log.error("No session store available for this web context");
				}
			}
		}

		return httpActionAdapter.adapt(action.getCode(), context);

	}
	
	private void logout(final C context) {
		SecurityContext securityContext = ((JaxRsContext) context).getRequestContext().getSecurityContext();
		log.debug("SecurityContext : {0}", securityContext);
        //TODO:delete only refresh token if refreshtoken was provided in the request
		//tokenService.removeRefreshToken(Long.parseLong(securityContext.getUserPrincipal().getName()));
        MultivaluedMap<String, String> queryParams = ((JaxRsContext) context).getRequestContext().getUriInfo().getQueryParameters();
        String refreshToken = null;
        if(queryParams.containsKey("refresh_token")) {
            refreshToken = queryParams.get("refresh_token").get(0);
        } 
        
        //if(refreshToken != null)
		//    tokenService.removeRefreshToken(refreshToken);
        log.debug("===============================================");
        //TODO: get userId and remove token for user and refreshToken
        Principal profile = securityContext.getUserPrincipal();
        if(profile != null) {                          
            log.debug ("Found profile : {}", profile);
            User user = userService.findById(Long.parseLong(profile.getName()));
            if(user != null && refreshToken != null) {
            	tokenService.removeRefreshToken(user.getId(), refreshToken);
                log.info ("Successfully removed refresh token");
            }
            log.debug("===============================================");
        } else {
        	log.error("Profile is null");
		}
	}
}
