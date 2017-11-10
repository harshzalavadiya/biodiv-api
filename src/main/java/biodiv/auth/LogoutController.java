package biodiv.auth;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.filters.JaxRsHttpActionAdapter;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.ResponseModel;

@Path("/logout")
public class LogoutController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final DefaultLogoutLogic<Object, JaxRsContext> DEFAULT_LOGIC = new BiodivLogoutLogic<Object, JaxRsContext>();

	private String defaultUrl = "/";

	private String logoutUrlPattern = "/logout";

	private Boolean localLogout = true;

	private Boolean destroySession = false;

	private Boolean centralLogout = false;

	static {
		DEFAULT_LOGIC.setProfileManagerFactory(BiodivJaxRsProfileManager::new);
	}

	@Context
	private Providers providers;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Pac4JSecurity(clients = "cookieClient,headerClient", authorizers = "isAuthenticated")
	public Response logout(@Pac4JProfile Optional<CommonProfile> profile,
			@Context final ContainerRequestContext requestContext, @Context SessionStore<WebContext> sessionStore) {

		log.debug("Logging out : " + profile);

		try {

			Config config = AuthUtils.getConfig();

			LogoutLogic<Object, JaxRsContext> ll = DEFAULT_LOGIC;

			final HttpActionAdapter adapter;
			if (config.getHttpActionAdapter() != null) {
				adapter = config.getHttpActionAdapter();
			} else {
				adapter = JaxRsHttpActionAdapter.INSTANCE;
			}

			ll.perform((new JaxRsContext(providers, requestContext, sessionStore)), config, adapter, null, "/logout",
					localLogout, destroySession, centralLogout);

			log.debug("Successfully logged out. Sending OK response");
			ResponseModel responseModel = new ResponseModel(Response.Status.OK, "Successfully logged out...");
			return Response.status(Response.Status.OK).entity(responseModel).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel responseModel = new ResponseModel(Response.Status.BAD_REQUEST, e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
		}
	}

}
