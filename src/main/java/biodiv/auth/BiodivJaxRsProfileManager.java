package biodiv.auth;

import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.context.WebContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.token.TokenService;

public class BiodivJaxRsProfileManager extends JaxRsProfileManager {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private TokenService tokenService = new TokenService();

	public BiodivJaxRsProfileManager(WebContext context) {
		super(context);
	}

	@Override
	public void logout() {
		SecurityContext securityContext = getJaxRsContext().getRequestContext().getSecurityContext();

		System.out.println(securityContext.getUserPrincipal().getName());
		System.out.println(securityContext.getUserPrincipal().getClass());

		tokenService.removeRefreshToken(Long.parseLong(securityContext.getUserPrincipal().getName()));

		//TODO:HACK to avoid null pointer at org.pac4j.core.context.WebContext.setSessionAttribute(WebContext.java:84)
		//super.logout();
	}
}
