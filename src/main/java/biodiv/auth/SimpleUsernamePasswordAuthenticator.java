package biodiv.auth;

import javax.inject.Inject;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.user.User;
import biodiv.user.UserService;

public class SimpleUsernamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private UserService userService;

	//@Inject
	MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");

	@Override
	public void validate(final UsernamePasswordCredentials credentials, final WebContext context)
			throws HttpAction, CredentialsException {
		if (credentials == null) {
			throwsException("No credential");
		}
		String username = credentials.getUsername();
		String password = credentials.getPassword();

		if (CommonHelper.isBlank(username)) {
			throwsException("Username cannot be blank");
		}
		if (CommonHelper.isBlank(password)) {
			throwsException("Password cannot be blank");
		}

		log.debug("Validating credentials : " + credentials);

		User user = userService.findByEmail(username);
		if (user == null) {
			throwsException("Not a valid user");
		}
		// TODO: using null salt and MD5 algorithm. Not safe. Upgrade to BCrypt
		else if (!passwordEncoder.isPasswordValid(user.getPassword(), password, null)) {
			throwsException("Password is not valid");
		} else {
			CommonProfile profile = userService.createUserProfile(user);
			log.debug("Setting profile in the context: " + profile);
			credentials.setUserProfile(profile);
		}
	}

	protected void throwsException(final String message) throws CredentialsException {
		throw new CredentialsException(message);
	}
}
