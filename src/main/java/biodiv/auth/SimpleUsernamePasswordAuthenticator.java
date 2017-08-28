package biodiv.auth;

import java.util.Set;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.user.Role;
import biodiv.user.User;
import biodiv.user.UserService;


public class SimpleUsernamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

	private final Logger log = LoggerFactory.getLogger(SimpleUsernamePasswordAuthenticator.class);

	UserService userService = new UserService();
	MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
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
        /*if (CommonHelper.areNotEquals(username, password)) {
            throwsException("Username : '" + username + "' does not match password");
        }*/
        
        log.debug("Validating credentials : "+credentials);
        
        User user = userService.findByEmail(username);
        if(user == null) {
        	throwsException("Not a valid user");
        }
        //TODO: using null salt and MD5 algorithm. Not safe. Upgrade to BCrypt
        else if(!passwordEncoder.isPasswordValid(user.getPassword(), password, null)) {
        	throwsException("Password is not valid");
        } else {
        	final CommonProfile profile = new CommonProfile();
        	profile.setId(user.getId());
        	profile.addAttribute(Pac4jConstants.USERNAME, user.getUsername());
        	profile.addAttribute(CommonProfileDefinition.EMAIL, user.getEmail());
        	Set<Role> roles = user.getRoles();
        	for(Role role : roles) {
        		profile.addRole(role.getAuthority());
        	}
        	log.debug("Setting profile in the context: "+profile);
        	
        	credentials.setUserProfile(profile);
        }
    }

    protected void throwsException(final String message) throws CredentialsException {
        throw new CredentialsException(message);
    }
}