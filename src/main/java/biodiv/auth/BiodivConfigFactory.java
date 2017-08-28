package biodiv.auth;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

public class BiodivConfigFactory implements ConfigFactory {

	@Override
	public Config build(Object... parameters) {
		
		System.out.println("building security configuration...");
		
		final Authenticator<UsernamePasswordCredentials> usernamePasswordAuthenticator = new SimpleUsernamePasswordAuthenticator();
		final DirectFormClient directFormClient = new DirectFormClient("username", "password",
				usernamePasswordAuthenticator);
		
		FacebookClient facebookClient = new FacebookClient("115305755799166", "efe695fb1a053bdd155e4a4ca153d409");
		//facebookClient.setStateData("biodiv-api-state");
		facebookClient.setAuthenticator(new CustomOAuth20Authenticator(facebookClient.getConfiguration()));
		facebookClient.setCallbackUrl("http://api.local.ibp.org/login/callback?client_name=facebookClient");
		
		Google2Client google2Client = new Google2Client("317806372709-roromqiujiji1po5jh8adpcr5um895mb.apps.googleusercontent.com", "x4QjtRV6n2f6cHjH8tl5epVn");
		//google2Client.setStateData("biodiv-api-state");
		google2Client.setAuthenticator(new CustomOAuth20Authenticator(google2Client.getConfiguration()));
		google2Client.setCallbackUrl("http://api.local.ibp.org/login/callback?client_name=google2Client");
		
		HeaderClient headerClient = new HeaderClient("X-AUTH-TOKEN", new JwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		
		//AnonymousClient anonymousClient = new AnonymousClient();
		
		Config config = new Config(facebookClient, google2Client, headerClient);
		// config.getClients().setCallbackUrlResolver(new JaxRsUrlResolver());

		// final JaxRsConfig config = new JaxRsConfig();
		// config.setClients(clients);

		config.addAuthorizer("ROLE_USER", new RequireAnyRoleAuthorizer("ROLE_USER"));
		config.addAuthorizer("ROLE_ADMIN", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
		//config.addAuthorizer("custom", new CustomAuthorizer());
		
		
		
		return config;
	}

}
