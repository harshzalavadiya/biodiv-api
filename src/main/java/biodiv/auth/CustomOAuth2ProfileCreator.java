package biodiv.auth;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.apache.commons.configuration2.Configuration;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;

import biodiv.user.User;
import biodiv.user.UserService;

public class CustomOAuth2ProfileCreator<C extends OAuthCredentials, U extends CommonProfile, O extends OAuthConfiguration, T extends Token>
		extends OAuth20ProfileCreator<OAuth20Profile> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private UserService userService;

	@Inject
	private SessionFactory sessionFactory;

	@Inject
	private Configuration config;

	public CustomOAuth2ProfileCreator(OAuth20Configuration configuration) {
		super(configuration);
	}

	@Override
	public OAuth20Profile create(final OAuth20Credentials credentials, final WebContext context) {

		boolean isActive = (sessionFactory.getCurrentSession().getTransaction() != null)
				? sessionFactory.getCurrentSession().getTransaction().isActive() : false;
		if (!isActive) {
			log.debug("Starting a new database transaction");
			sessionFactory.getCurrentSession().beginTransaction();
		} else {
			log.debug("Using existing database transaction");
		}

		try {

			final OAuth2AccessToken token = getAccessToken(credentials);
			OAuth20Profile oAuthProfile = retrieveUserProfileFromToken(token);
			log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.debug(oAuthProfile.toString());
			log.debug(oAuthProfile.getEmail());
			log.debug(oAuthProfile.getLinkedId());
			log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			String email = oAuthProfile.getEmail();
			User user = null;

			try {
				user = userService.findByEmail(email);
				if (user != null) {
					userService.updateUserProfile(oAuthProfile, user);
				}
			} catch (NotFoundException e) {
				Map<String, String> p = new HashMap<String, String>();

				if (oAuthProfile instanceof FacebookProfile) {
					FacebookProfile fbProfile = (FacebookProfile) oAuthProfile;

					p.put("username", fbProfile.getEmail());
					p.put("name", fbProfile.getDisplayName());
					p.put("email", fbProfile.getEmail());
					
					if (fbProfile.getWebsite() != null)
						p.put("website", fbProfile.getWebsite());
					
					if (fbProfile.getPictureUrl() != null)
						p.put("profilePic", fbProfile.getPictureUrl().toString());
					
					// not reading aboutMe as the redirect request to create register
					// is GET and about me can exceed the char limit of get
					// request
					// if (fbProfile.getAbout() != null)
					// p.put("aboutMe", fbProfile.getAbout());
					
					if (fbProfile.getLocation() != null)
						p.put("location", fbProfile.getLocation());
					
					p.put("facebookUser", "true");
					
					// p.put("openId", fbProfile.getId());
					
					if (fbProfile.getGender() != null)
						p.put("sexType", fbProfile.getGender().toString());
					
					p.put("link", fbProfile.getProfileUrl().toString());
					
					if (fbProfile.getTimezone() != null)
						p.put("timezone", fbProfile.getTimezone().floatValue() + "");
					
				} else if (oAuthProfile instanceof org.pac4j.oauth.profile.google2.Google2Profile) {

					Google2Profile gProfile = (Google2Profile) oAuthProfile;

					p.put("username", gProfile.getEmail());
					p.put("email", gProfile.getEmail());

					if (gProfile.getDisplayName() != null)
						p.put("name", gProfile.getDisplayName());

					if (gProfile.getProfileUrl() != null)
						p.put("website", gProfile.getProfileUrl().toString());

					if (gProfile.getPictureUrl() != null)
						p.put("profilePic", gProfile.getPictureUrl().toString());

					// if(gProfile.getAbout() != null)
					// p.put("aboutMe",
					// gProfile.getAbout());

					if (gProfile.getLocation() != null)
						p.put("location", gProfile.getLocation());

					// p.put("openId", gProfile.getId());

					if (gProfile.getGender() != null)
						p.put("sexType", gProfile.getGender().toString());

					if (gProfile.getProfileUrl() != null)
						p.put("link", gProfile.getProfileUrl().toString());

					// if(gProfile.getTimezone() != null)
					// p.put("timezone",
					// gProfile.getTimezone());
				}
				p.put("redirect_url", config.getString("createSocialAccountFromProfile"));

				if (!isActive) {
					log.debug("Committing the database transaction");
					sessionFactory.getCurrentSession().getTransaction().commit();
				}

				throwsException(e.getMessage(), p);
			}

			return oAuthProfile;

		} catch (final OAuthException e) {
			throw new TechnicalException(e);
		} catch (HttpAction e) {
			throw new TechnicalException(e);
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				log.warn("Trying to rollback database transaction after exception");
				sessionFactory.getCurrentSession().getTransaction().rollback();
			} catch (Throwable rbEx) {
				log.error("Could not rollback transaction after exception!", rbEx);
				throw new TechnicalException(rbEx);
			}

			throw new TechnicalException(e);
			// throw e;
		} finally {

		}

	}

	protected void throwsException(final String message, final Map<String, String> details)
			throws AccountNotFoundException {
		throw new AccountNotFoundException(message, details);
	}
}
