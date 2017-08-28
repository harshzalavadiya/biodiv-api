package biodiv.auth;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;

@Provider
public class Pac4JFeature implements Feature {

	@Override
	public boolean configure(FeatureContext context) {
		context
				// The JaxRsContextFactoryProvider enables generic JAX-RS based
				// pac4j functioning, without session handling (i.e., it will
				// only work with direct clients)
				.register(new JaxRsConfigProvider(getConfig()))

				// The Pac4JProfileValueFactoryProvider enables injection of the
				// security profile in resource method (for Apache Jersey)
				.register(new Pac4JValueFactoryProvider.Binder())
		
				// The Pac4JSecurityFeature enables annotation-based activation
				// of the filters at the resource method level
				.register(new Pac4JSecurityFeature())
				
				//The ServletJaxRsContextFactoryProvider provides session handling (and thus indirect clients support) by replacing the generic JaxRsContextFactoryProvider (for Servlet-based JAX-RS implementations, e.g., Jersey on Netty or Grizzly Servlet, Resteasy on Undertow).
	            .register(new ServletJaxRsContextFactoryProvider());		

		return true;
	}

	private static Config getConfig() {
		// turn the properties file into a map of properties
		// TODO
		final Map<String, Object> properties = new HashMap<String, Object>();
		/*
		 * properties.put(PropertiesConfigFactory.FACEBOOK_ID,
		 * this.pac4jProperties.getFacebook().getId());
		 * properties.put(PropertiesConfigFactory.FACEBOOK_SECRET,
		 * this.pac4jProperties.getFacebook().getSecret());
		 * properties.put(PropertiesConfigFactory.FACEBOOK_SCOPE,
		 * this.pac4jProperties.getFacebook().getScope());
		 * properties.put(PropertiesConfigFactory.FACEBOOK_FIELDS,
		 * this.pac4jProperties.getFacebook().getFields());
		 * properties.put(PropertiesConfigFactory.TWITTER_ID,
		 * this.pac4jProperties.getTwitter().getId());
		 * properties.put(PropertiesConfigFactory.TWITTER_SECRET,
		 * this.pac4jProperties.getTwitter().getSecret());
		 * properties.put(PropertiesConfigFactory.CAS_LOGIN_URL,
		 * this.pac4jProperties.getCas().getLoginUrl());
		 */

		final ConfigFactory configFactory = new BiodivConfigFactory();
		return configFactory.build(properties);
	}
}
