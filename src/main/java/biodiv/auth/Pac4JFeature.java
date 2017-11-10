package biodiv.auth;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

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
				.register(new JaxRsConfigProvider(AuthUtils.getConfig()))

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
}
