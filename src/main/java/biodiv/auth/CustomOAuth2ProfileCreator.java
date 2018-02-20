package biodiv.auth;

import javax.inject.Inject;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
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

	public CustomOAuth2ProfileCreator(OAuth20Configuration configuration) {
		super(configuration);
	}

	@Override
	public OAuth20Profile create(final OAuth20Credentials credentials, final WebContext context) {
		try {
    		boolean isActive = (sessionFactory.getCurrentSession().getTransaction() != null) ? sessionFactory.getCurrentSession().getTransaction().isActive() : false; 
    		if ( !isActive) {  
                log.debug("Starting a new database transaction");  
                sessionFactory.getCurrentSession().beginTransaction();  
             }  else {
            	 log.debug("Using existing database transaction");
             }

			final OAuth2AccessToken token = getAccessToken(credentials);
			OAuth20Profile profile = retrieveUserProfileFromToken(token);
			log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			log.debug(profile.toString());
			log.debug(profile.getEmail());
			log.debug(profile.getLinkedId());
			log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			String email = profile.getEmail();
			User user = userService.findByEmail(email);
			if (user == null) {
				throwsException("Not a valid user");
			} else {
				userService.updateUserProfile(profile, user);
			}
    		if (!isActive) {  
                log.debug("Committing the database transaction");  
                sessionFactory.getCurrentSession().getTransaction().commit();  
             }  
 
			return profile;
		} catch (final OAuthException e) {
			throw new TechnicalException(e);
		} catch (HttpAction e) {
			throw new TechnicalException(e);
		} catch (CredentialsException e) {
			throw new TechnicalException(e);
		} catch(Throwable e){
    		e.printStackTrace();
    		try {  
                log.warn("Trying to rollback database transaction after exception");  
                sessionFactory.getCurrentSession().getTransaction().rollback();  
            } catch (Throwable rbEx) {  
                log.error("Could not rollback transaction after exception!", rbEx);  
			    throw new TechnicalException(rbEx);
            }  

			throw new TechnicalException(e);
    	}

	}

	protected void throwsException(final String message) throws CredentialsException {
		throw new CredentialsException(message);
	}
}
