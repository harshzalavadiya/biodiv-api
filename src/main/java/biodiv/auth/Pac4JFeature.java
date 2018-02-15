package biodiv.auth;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.CookieClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;

//@Provider
public class Pac4JFeature implements Feature {

	@Override
	public boolean configure(FeatureContext context) {
		context
				// The JaxRsContextFactoryProvider enables generic JAX-RS based
				// pac4j functioning, without session handling (i.e., it will
				// only work with direct clients)
				.register(new JaxRsConfigProvider(buildConfig()))

				// The Pac4JProfileValueFactoryProvider enables injection of the
				// security profile in resource method (for Apache Jersey)
				.register(new Pac4JValueFactoryProvider.Binder())
		
				// The Pac4JSecurityFeature enables annotation-based activation
				// of the filters at the resource method level
				.register(new Pac4JSecurityFeature());
				
				//The ServletJaxRsContextFactoryProvider provides session handling (and thus indirect clients support) by replacing the generic JaxRsContextFactoryProvider (for Servlet-based JAX-RS implementations, e.g., Jersey on Netty or Grizzly Servlet, Resteasy on Undertow).
	            //.register(new ServletJaxRsContextFactoryProvider());		

		return true;
	}
	
	public Config buildConfig() {
	System.out.println("building security configuration...");
		
		FacebookClient facebookClient = new FacebookClient("115305755799166", "efe695fb1a053bdd155e4a4ca153d409");
		//facebookClient.setStateData("biodiv-api-state");
		facebookClient.setAuthenticator(new CustomOAuth20Authenticator(facebookClient.getConfiguration()));
		facebookClient.setCallbackUrl("http://api.local.ibp.org/login/callback?client_name=facebookClient");
		
		Google2Client google2Client = new Google2Client("317806372709-roromqiujiji1po5jh8adpcr5um895mb.apps.googleusercontent.com", "x4QjtRV6n2f6cHjH8tl5epVn");
		//google2Client.setStateData("biodiv-api-state");
		google2Client.setAuthenticator(new CustomOAuth20Authenticator(google2Client.getConfiguration()));
		google2Client.setProfileCreator(new CustomOAuth2ProfileCreator(google2Client.getConfiguration()));
		google2Client.setCallbackUrl("http://api.local.ibp.org/login/callback?client_name=google2Client");
		
		HeaderClient headerClient = new HeaderClient("X-AUTH-TOKEN", new CustomJwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		
		CookieClient cookieClient = new CookieClient("BAToken", new CustomJwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		
		//AnonymousClient anonymousClient = new AnonymousClient();
		Clients clients = new Clients(facebookClient, google2Client, headerClient, cookieClient);
		clients.setDefaultClient(cookieClient);
		Config config = new Config(clients);
		// config.getClients().setCallbackUrlResolver(new JaxRsUrlResolver());

		// final JaxRsConfig config = new JaxRsConfig();
		// config.setClients(clients);

		config.addAuthorizer("ROLE_USER", new RequireAnyRoleAuthorizer("ROLE_USER"));
		config.addAuthorizer("ROLE_ADMIN", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
		//config.addAuthorizer("custom", new CustomAuthorizer());
		
		
	
		return config;
	}
}
