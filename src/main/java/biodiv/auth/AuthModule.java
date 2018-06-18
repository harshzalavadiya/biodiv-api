package biodiv.auth;

import org.apache.commons.configuration2.Configuration;
import org.elasticsearch.common.inject.assistedinject.FactoryProvider;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.CookieClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider.Binder;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.servlet.ServletModule;

import biodiv.auth.register.GoogleRecaptchaCheck;
import biodiv.auth.register.RegisterController;
import biodiv.auth.register.RegistrationCodeFactory;
import biodiv.auth.token.Token;
import biodiv.auth.token.TokenDao;
import biodiv.auth.token.TokenService;
import biodiv.auth.register.RegisterDao;
import biodiv.auth.register.RegisterMailingService;
import biodiv.auth.register.RegisterService;
import biodiv.auth.register.RegistrationCode;

public class AuthModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configureServlets() {
		log.debug("Configuring AuthModule Servlets");

		bind(Token.class);

		bind(TokenDao.class).in(Singleton.class);
		bind(TokenService.class).in(Singleton.class);

		bind(SimpleUsernamePasswordAuthenticator.class).in(Singleton.class);
		bind(BiodivJaxRsProfileManager.class).in(Singleton.class);

		bind(LoginController.class).in(Singleton.class);
		bind(LogoutController.class).in(Singleton.class);
		bind(RegistrationCode.class);
		bind(RegisterDao.class).in(Singleton.class);
		bind(RegisterService.class).in(Singleton.class);
		bind(RegisterController.class).in(Singleton.class);
		bind(GoogleRecaptchaCheck.class);
		bind(RegisterMailingService.class);
		
		//bind(JaxRsContextFactoryProvider.class).asEagerSingleton();
		bind(ServletJaxRsContextFactoryProvider.class).asEagerSingleton();
		bind(Pac4JSecurityFeature.class).asEagerSingleton();
		//bind(Binder.class).asEagerSingleton();	
		
		//bind(ProfileManager.class).toProvider((Class<? extends Provider<? extends ProfileManager>>) BiodivProviderManagerFactory.class);
		//bind(RegistrationCodeFactory.class).toProvider(
		//	    FactoryProvider<F>.newFactory(RegistrationCodeFactory.class, RegistrationCode.class));
		install(new FactoryModuleBuilder()
			     //.implement(Payment.class, RealPayment.class)
			     .build(RegistrationCodeFactory.class));
	}

	
	@Provides @Singleton
	protected FacebookClient provideFacebookClient(Injector injector) {
		log.debug("Creating facebook client");
		Configuration config = injector.getInstance(Configuration.class);
		final String fbId = config.getString("fbId");
		final String fbSecret = config.getString("fbSecret");
		final FacebookClient facebookClient = new FacebookClient(fbId, fbSecret);
		//facebookClient.setStateData("biodiv-api-state");
	    facebookClient.setScope("email,user_location");
	    facebookClient.setFields("id,name,gender,email,location");
		CustomOAuth20Authenticator customOAuth20Authenticator = new CustomOAuth20Authenticator(facebookClient.getConfiguration());
        CustomOAuth2ProfileCreator customOAuth2ProfileCreator = new CustomOAuth2ProfileCreator(facebookClient.getConfiguration());
        //used to inject userService
        injector.injectMembers(customOAuth2ProfileCreator);
        
		facebookClient.setAuthenticator(customOAuth20Authenticator);
		facebookClient.setProfileCreator(customOAuth2ProfileCreator);
		facebookClient.setCallbackUrl(config.getString("fbCallback"));
		return facebookClient;
	}

	@Provides @Singleton
	protected Google2Client provideGoole2Client(Injector injector) {
		log.debug("Creating google client");
		Configuration config = injector.getInstance(Configuration.class);
		final String googleId = config.getString("googleId");
		final String googleSecret = config.getString("googleSecret");
		final Google2Client google2Client = new Google2Client(googleId, googleSecret);
		// google2Client.setStateData("biodiv-api-state");
        CustomOAuth20Authenticator customOAuth20Authenticator = new CustomOAuth20Authenticator(google2Client.getConfiguration());
        CustomOAuth2ProfileCreator customOAuth2ProfileCreator = new CustomOAuth2ProfileCreator(google2Client.getConfiguration());
        //used to inject userService
        injector.injectMembers(customOAuth2ProfileCreator);

		google2Client.setAuthenticator(customOAuth20Authenticator);
		google2Client.setProfileCreator(customOAuth2ProfileCreator);
		google2Client.setCallbackUrl(config.getString("googleCallback"));

		return google2Client;
	}

	@Provides @Singleton
	protected CookieClient provideCookieClient(Injector injector) {
		log.debug("Creating cookie client");
		Configuration config = injector.getInstance(Configuration.class);
		final CookieClient cookieClient = new CookieClient(config.getString("accessToken_cookieName"), new CustomJwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		return cookieClient;
	}

	@Provides @Singleton
	protected HeaderClient provideHeaderClient(Injector injector) {
		log.debug("Creating header client");
		Configuration config = injector.getInstance(Configuration.class);
		final HeaderClient headerClient = new HeaderClient(config.getString("headerName"), new CustomJwtAuthenticator(
				new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(Constants.JWT_SALT)));
		return headerClient;
	}

	@Provides @Singleton
	protected Config provideConfig(FacebookClient facebookClient, Google2Client google2Client,
			HeaderClient headerClient, CookieClient cookieClient, BiodivLogoutLogic biodivLogoutLogic) {
		log.debug("Creating pac4j security configuration");
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
        
        google2Client.setUrlResolver(new CustomJaxRsUrlResolver());
//        config.getClients().setCallbackUrlResolver(new CustomJaxRsUrlResolver());
//        config.getClients().setAjaxRequestResolver(new JaxRsAjaxRequestResolver());

		log.trace("Setting LogoutLogic in pac4jConfig");
        config.setLogoutLogic(biodivLogoutLogic);
        config.setProfileManagerFactory(BiodivJaxRsProfileManager::new);
		return config;
	}
	
	@Provides @Singleton
	protected BiodivLogoutLogic<Object, JaxRsContext> provideBiodivLogoutLogic() {
		return new BiodivLogoutLogic<Object, JaxRsContext>();
	}
	
	@Provides @Singleton
	protected Binder provideBinder() {
		return new Binder(null, null, null);
	}
	/*
	class MyProfileManagerFactoryBuilder implements ProfileManagerFactoryBuilder {

		@Override
		public ProfileManagerFactory apply(Parameter t) {
			return new MyProfileManagerFactory();
		}
		
	}
	
	class MyProfileManagerFactory implements ProfileManagerFactory {
		   @Context
	        private Providers providers;

	        protected JaxRsContext getContext() {
	            JaxRsContext context = ProvidersHelper.getContext(providers, JaxRsContextFactory.class)
	                    .provides(getContainerRequest());
	            assert context != null;
	            return context;
	        }
	        
		@Override
		public ProfileManager<CommonProfile> provide() {
			return new ProfileManager<>(getContext());
		}
		
	}*/

}
