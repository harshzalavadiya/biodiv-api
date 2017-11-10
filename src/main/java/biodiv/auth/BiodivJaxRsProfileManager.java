package biodiv.auth;

import java.security.Principal;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.context.WebContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.auth.token.TokenService;
import biodiv.user.User;
import biodiv.user.UserService;

public class BiodivJaxRsProfileManager extends JaxRsProfileManager {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private TokenService tokenService = new TokenService();
	private UserService userService = new UserService();
	
	public BiodivJaxRsProfileManager(WebContext context) {
		super(context);
	}

	@Override
	public void logout() {
		SecurityContext securityContext = getJaxRsContext().getRequestContext().getSecurityContext();

        //TODO:delete only refresh token if refreshtoken was provided in the request
		//tokenService.removeRefreshToken(Long.parseLong(securityContext.getUserPrincipal().getName()));
        MultivaluedMap<String, String> queryParams = getJaxRsContext().getRequestContext().getUriInfo().getQueryParameters();
        String refreshToken = null;
        if(queryParams.containsKey("refresh_token")) {
            refreshToken = queryParams.get("refresh_token").get(0);
        } 
        
        //if(refreshToken != null)
		//    tokenService.removeRefreshToken(refreshToken);
        System.out.println("===============================================");
        //TODO: get userId and remove token for user and refreshToken
        Principal profile = securityContext.getUserPrincipal();
        if(profile != null) {                          
            User user = userService.findById(Long.parseLong(profile.getName()));
            if(user != null && refreshToken != null) {
            	tokenService.removeRefreshToken(user.getId(), refreshToken);
                log.info ("Successfully removed refresh token");
            }
            System.out.println("===============================================");
        }

        //TODO:HACK to avoid null pointer at org.pac4j.core.context.WebContext.setSessionAttribute(WebContext.java:84)
		//super.logout();
	}
}
