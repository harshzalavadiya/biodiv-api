package biodiv.auth;

import org.pac4j.core.context.WebContext;

public class Google2Client extends org.pac4j.oauth.client.Google2Client {
	public Google2Client() {
	}

	public Google2Client(final String key, final String secret) {
		setKey(key);
		setSecret(secret);
	}

	@Override
	protected void clientInit(final WebContext context) {
		super.clientInit(context);
		// TODO: state param is used for csrf protection. shd ideally be true
		configuration.setWithState(false);
	}
}
