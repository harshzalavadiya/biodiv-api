package biodiv.auth;

import javax.inject.Inject;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.CookieClient;
import org.pac4j.http.client.direct.HeaderClient;

public class Pac4jConfig {

	public Config config;
	
	@Inject
	Pac4jConfig() {
		System.out.println("Pac4jConfig constructor");
		FacebookClient facebookClient = provideFacebookClient();
		Google2Client google2Client = provideGoole2Client();
		HeaderClient headerClient = provideHeaderClient();
		CookieClient cookieClient = provideCookieClient();

		final Clients clients = new Clients(facebookClient, google2Client, headerClient, cookieClient);
		clients.setDefaultClient(cookieClient);

		Config config = new Config();
		config.setClients(clients);
		config.addAuthorizer("ROLE_USER", new RequireAnyRoleAuthorizer("ROLE_USER"));
		config.addAuthorizer("ROLE_ADMIN", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
		// config.addAuthorizer("custom", new CustomAuthorizer());
		this.config = config;
	}
	
	protected FacebookClient provideFacebookClient() {
		final String fbId = "115305755799166";// configuration.getString("fbId");
		final String fbSecret = "efe695fb1a053bdd155e4a4ca153d409";// configuration.getString("fbSecret");
		final FacebookClient facebookClient = new FacebookClient("115305755799166", "efe695fb1a053bdd155e4a4ca153d409");
		// facebookClient.setStateData("biodiv-api-state");
		facebookClient.setAuthenticator(new CustomOAuth20Authenticator(facebookClient.getConfiguration()));
		facebookClient.setCallbackUrl("http://api.local.ibp.org/login/callback?client_name=facebookClient");
		return facebookClient;
	}

	protected Google2Client provideGoole2Client() {
		final String googleId = "317806372709-roromqiujiji1po5jh8adpcr5um895mb.apps.googleusercontent.com";// configuration.getString("fbId");
		final String googleSecret = "x4QjtRV6n2f6cHjH8tl5epVn";// configuration.getString("fbSecret");
		final Google2Client google2Client = new Google2Client(googleId, googleSecret);
		// google2Client.setStateData("biodiv-api-state");
		google2Client.setAuthenticator(new CustomOAuth20Authenticator(google2Client.getConfiguration()));
		google2Client.setProfileCreator(new CustomOAuth2ProfileCreator(google2Client.getConfiguration()));
		google2Client.setCallbackUrl("http://api.local.ibp.org/login/callback?client_name=google2Client");

		return google2Client;
	}

	protected CookieClient provideCookieClient() {
		final CookieClient cookieClient = new CookieClient("BAToken", new CustomJwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		return cookieClient;
	}

	protected HeaderClient provideHeaderClient() {
		final HeaderClient headerClient = new HeaderClient("X-AUTH-TOKEN", new CustomJwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		return headerClient;
	}

	public Config provideConfig() {
		System.out.println("provideConfig provideConfig provideConfig provideConfig provideConfig");
		FacebookClient facebookClient = provideFacebookClient();
		Google2Client google2Client = provideGoole2Client();
		HeaderClient headerClient = provideHeaderClient();
		CookieClient cookieClient = provideCookieClient();

		final Clients clients = new Clients(facebookClient, google2Client, headerClient, cookieClient);
		clients.setDefaultClient(cookieClient);

		final Config config = new Config(clients);
		config.addAuthorizer("ROLE_USER", new RequireAnyRoleAuthorizer("ROLE_USER"));
		config.addAuthorizer("ROLE_ADMIN", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
		config.addAuthorizer("ROLE_USERGROUP_FOUNDER", new RequireAnyRoleAuthorizer("ROLE_USERGROUP_FOUNDER"));
		config.addAuthorizer("ROLE_USERGROUP_EXPERT", new RequireAnyRoleAuthorizer("ROLE_USERGROUP_EXPERT"));
		config.addAuthorizer("ROLE_USERGROUP_MEMBER", new RequireAnyRoleAuthorizer("ROLE_USERGROUP_MEMBER"));
		config.addAuthorizer("ROLE_SPECIES_ADMIN", new RequireAnyRoleAuthorizer("ROLE_SPECIES_ADMIN"));
		config.addAuthorizer("ROLE_CEPF_ADMIN", new RequireAnyRoleAuthorizer("ROLE_CEPF_ADMIN"));
		// config.addAuthorizer("custom", new CustomAuthorizer());
		return config;
	}

}
