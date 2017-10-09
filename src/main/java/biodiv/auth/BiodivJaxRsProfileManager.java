package biodiv.auth;

import javax.ws.rs.core.MultivaluedMap;
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

        //TODO:delete only refresh token if refreshtoken was provided in the request
		//tokenService.removeRefreshToken(Long.parseLong(securityContext.getUserPrincipal().getName()));
        MultivaluedMap<String, String> queryParams = getJaxRsContext().getRequestContext().getUriInfo().getQueryParameters();
        String refreshToken = null;
        if(queryParams.containsKey("refresh_token")) {
            refreshToken = queryParams.get("refresh_token").get(0);
        } 
        
        if(refreshToken != null)
		    tokenService.removeRefreshToken(refreshToken);
        //TODO: get userId and remove token for user and refreshToken
        /*org.pac4j.jax.rs.pac4j.JaxRsProfileManager.PrincipalImpl profile = securityContext.getUserPrincipal();
        if(profile) {      
            String email = null;        
            if(profile instanceof org.pac4j.oauth.profile.google2.Google2Profile) {                                                                 
                final List list = profile.getEmails(); 
                println list;                   
                if (list != null && !list.isEmpty()) {
                    email= list.get(0).email;   
                }          
            } else {
                email = profile.email;      
            }              
            User user = userService.findByEmail(email);
		    tokenService.removeRefreshToken(user.id, getJaxRsContext().getRequestContext().getProperty("refresh_token"));
        }*/

        //TODO:HACK to avoid null pointer at org.pac4j.core.context.WebContext.setSessionAttribute(WebContext.java:84)
		//super.logout();
	}
}
