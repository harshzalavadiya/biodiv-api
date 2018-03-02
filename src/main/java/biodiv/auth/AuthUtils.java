package biodiv.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.jax.rs.annotations.Pac4JProfile;

public class AuthUtils {

	private static final int ACCESS_TOKEN_EXPIRY_TIME_IN_DAYS = 10;
	private static final int EXPIRY_TIME_IN_DAYS = 30;
	
//	public static Config getConfig() {
		//ProvidersHelper.getContext(providers, Config.class);
		// turn the properties file into a map of properties
		// TODO
//		final Map<String, Object> properties = new HashMap<String, Object>();
		/*
		 * properties.put(PropertiesConfigFactory.FACEBOOK_ID,
		 * this.pac4jProperties.getFacebook().getId());
		 * properties.put(PropertiesConfigFactory.FACEBOOK_SECRET,
		 * this.pac4jProperties.getFacebook().getSecret());
		 * properties.put(PropertiesConfigFactory.FACEBOOK_SCOPE,
		 * this.pac4jProperties.getFacebook().getScope());
		 * properties.put(PropertiesConfigFactory.FACEBOOK_FIELDS,
		 * this.pac4jProperties.getFacebook().getFields());
		 * properties.put(PropertiesConfigFactory.TWITTER_ID,
		 * this.pac4jProperties.getTwitter().getId());
		 * properties.put(PropertiesConfigFactory.TWITTER_SECRET,
		 * this.pac4jProperties.getTwitter().getSecret());
		 * properties.put(PropertiesConfigFactory.CAS_LOGIN_URL,
		 * this.pac4jProperties.getCas().getLoginUrl());
		 */

//		final ConfigFactory configFactory = new BiodivConfigFactory();
//		return configFactory.build(properties);
//	}
	
	public static CommonProfile createUserProfile(Long userId, String username, String email, List authorities) {
		CommonProfile profile = new CommonProfile();
		updateUserProfile(profile, userId, username, email, authorities);
		return profile;
	}
	
	public static void updateUserProfile(CommonProfile profile, Long userId, String username, String email, List authorities) {
		if(profile == null) return;
		profile.setId(userId);
		profile.addAttribute("id", userId);
		profile.addAttribute(Pac4jConstants.USERNAME, username);
		profile.addAttribute(CommonProfileDefinition.EMAIL, email);
		profile.addAttribute(JwtClaims.EXPIRATION_TIME, getAccessTokenExpiryDate());
		profile.addAttribute(JwtClaims.ISSUED_AT, new Date());
		for (Object authority: authorities) {
			profile.addRole((String)authority);
		}
	}

	public static Date getAccessTokenExpiryDate() {
		final Date now = new Date();
		long expDate = now.getTime() + ACCESS_TOKEN_EXPIRY_TIME_IN_DAYS * (24 * 3600 * 1000);
		return new Date(expDate);

	}

	public static Date getRefreshTokenExpiryDate() {
		final Date now = new Date();
		long expDate = now.getTime() + EXPIRY_TIME_IN_DAYS * (24 * 3600 * 1000);
		return new Date(expDate);
	}

	public static CommonProfile currentUser(HttpServletRequest request) {
		ProfileManager manager = new ProfileManager(new J2EContext(request, null));
		Optional<CommonProfile> profile = manager.get(true);
		return profile.get();		
	}
	
	public static Boolean isLoggedIn() {
		
		return false;
	}
}
