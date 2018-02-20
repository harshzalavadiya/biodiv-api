package biodiv.auth;

import java.io.IOException;

import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.credentials.authenticator.OAuth20Authenticator;
import org.pac4j.oauth.exception.OAuthCredentialsException;

import com.github.scribejava.core.model.OAuth2AccessToken;

public class CustomOAuth20Authenticator extends OAuth20Authenticator {

    public CustomOAuth20Authenticator(OAuth20Configuration configuration) {
		super(configuration);
	}

	@Override
	    protected void retrieveAccessToken(final OAuthCredentials credentials) throws HttpAction, OAuthCredentialsException {
	        OAuth20Credentials oAuth20Credentials = (OAuth20Credentials) credentials;
	        // no request token saved in context and no token (OAuth v2.0)
	        final String code = oAuth20Credentials.getCode();
	        logger.debug("code: {}", code);
	        final OAuth2AccessToken accessToken;
	        try {
	            accessToken = this.configuration.getService().getAccessToken(code);
	        } catch (IOException ex) {
	            throw new HttpCommunicationException("Error getting token:" + ex.getMessage());
	        }
	        logger.debug("accessToken: {}", accessToken);
	        oAuth20Credentials.setAccessToken(accessToken);
	    }

}
