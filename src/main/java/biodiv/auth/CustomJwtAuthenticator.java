package biodiv.auth;

import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.signature.SignatureConfiguration;

public class CustomJwtAuthenticator extends JwtAuthenticator {

    public CustomJwtAuthenticator(final SignatureConfiguration signatureConfiguration) {
        super(signatureConfiguration);
    }
            
}   
