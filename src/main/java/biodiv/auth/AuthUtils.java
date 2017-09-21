package biodiv.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.jwt.JwtClaims;

import biodiv.user.Role;
import biodiv.user.User;

public class AuthUtils {

	private static final int EXPIRY_TIME_IN_HOURS = 24;
	private static final int EXPIRY_TIME_IN_DAYS = -30;
	
	public static Config getConfig() {
		//ProvidersHelper.getContext(providers, Config.class);
		// turn the properties file into a map of properties
		// TODO
		final Map<String, Object> properties = new HashMap<String, Object>();
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

		final ConfigFactory configFactory = new BiodivConfigFactory();
		return configFactory.build(properties);
	}
	
	public static CommonProfile createUserProfile(User user) {
		if(user == null) return null;
		final CommonProfile profile = new CommonProfile();
		profile.setId(user.getId());
		profile.addAttribute(Pac4jConstants.USERNAME, user.getUsername());
		profile.addAttribute(CommonProfileDefinition.EMAIL, user.getEmail());
		profile.addAttribute(JwtClaims.EXPIRATION_TIME, getAccessTokenExpiryDate());
		Set<Role> roles = user.getRoles();
		for (Role role : roles) {
			profile.addRole(role.getAuthority());
		}
		return profile;
	}

	public static Date getAccessTokenExpiryDate() {
		Calendar cal = Calendar.getInstance();
		// cal.add(Calender.DATE, 7);
		cal.add(Calendar.SECOND, 60);
		//cal.add(Calendar.HOUR_OF_DAY, EXPIRY_TIME_IN_HOURS);
		return cal.getTime();
	}
	
	public static Date getRefreshTokenExpiryDate() {
		Calendar cal = Calendar.getInstance();
		// cal.add(Calender.DATE, 7);
		cal.add(Calendar.DATE, EXPIRY_TIME_IN_DAYS);
		return cal.getTime();
	}
}
