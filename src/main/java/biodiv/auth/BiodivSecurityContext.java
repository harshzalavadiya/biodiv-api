package biodiv.auth;

import java.security.Principal;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

import biodiv.user.Role;
import biodiv.user.User;

public class BiodivSecurityContext implements SecurityContext {

	private User user;
	private String schema;
	
	public BiodivSecurityContext(User user, String schema) {
		this.user = user;
		this.schema = schema;
	}
	
	@Override
	public Principal getUserPrincipal() {
		return this.user;
	}

	@Override
	public boolean isUserInRole(String role) {
		return this.user.hasRole(role);
	}

	@Override
	public boolean isSecure() {
		return "https".equals(this.schema);
	}

	@Override
	public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
	}

}
