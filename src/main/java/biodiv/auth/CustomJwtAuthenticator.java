package biodiv.auth;

import java.util.HashMap;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtProfile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

public class CustomJwtAuthenticator extends JwtAuthenticator {

	public CustomJwtAuthenticator(final SignatureConfiguration signatureConfiguration) {
		super(signatureConfiguration);
	}

	public String getProfileId(String accessToken) throws Exception {
		JWT jwt = JWTParser.parse(accessToken);
		JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
		String subject = claimSet.getSubject();
		if (subject == null) {
            throw new Exception("JWT must contain a subject ('sub' claim)");
		}
		CommonProfileDefinition profileDefinition = new CommonProfileDefinition<>(x -> new JwtProfile());
		final CommonProfile profile = ProfileHelper.restoreOrBuildProfile(profileDefinition, subject, new HashMap<>());
		return profile.getId();
	}
	
}
