package biodiv.auth;

import java.util.regex.Pattern;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiodivLogoutLogic<R, C extends WebContext> extends DefaultLogoutLogic<R, C> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
			final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputLocalLogout,
			final Boolean inputDestroySession, final Boolean inputCentralLogout) {
		
		logger.debug("=== LOGOUT ===");

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

		// compute redirection URL
		final String url = context.getRequestParameter(Pac4jConstants.URL);
		String redirectUrl = defaultUrl;
		if (url != null && Pattern.matches(logoutUrlPattern, url)) {
			redirectUrl = url;
		}
		logger.debug("redirectUrl: {}", redirectUrl);
		HttpAction action;
		if (redirectUrl != null) {
			action = HttpAction.redirect("redirect", context, redirectUrl);
		} else {
			action = HttpAction.ok("ok", context);
		}

		// local logout if requested or multiple profiles
		if (localLogout) {
			logger.debug("Performing application logout");
			manager.logout();
			if (destroySession) {
				final SessionStore<WebContext> sessionStore = context.getSessionStore();
				if (sessionStore != null) {
					final boolean removed = sessionStore.destroySession(context);
					if (!removed) {
						logger.error(
								"Unable to destroy the web session. The session store may not support this feature");
					}
				} else {
					logger.error("No session store available for this web context");
				}
			}
		}

		return httpActionAdapter.adapt(action.getCode(), context);

	}
}
