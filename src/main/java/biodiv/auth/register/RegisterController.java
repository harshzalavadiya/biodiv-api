package biodiv.auth.register;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.beans.BeansUtils;

import biodiv.auth.LoginController;
import biodiv.auth.token.Token;
import biodiv.auth.token.TokenService;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.common.MailService;
import biodiv.common.MessageService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.userGroup.UserGroup;
import biodiv.util.Utils;

@Path("/register")
public class RegisterController {
	private final Logger log = LoggerFactory.getLogger(LoginController.class);

	private UserService userService = new UserService();
	private LanguageService languageService = new LanguageService();
	private MessageService messageService = new MessageService();
	private MailService mailService = new MailService();

	/**
	 * 
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@BeanParam RegisterCommand registerCommand, @Context HttpServletRequest request) {
		log.debug("Registering user " + registerCommand.toString());

		try {
			Language userLanguage = languageService.getCurrentLanguage(request);

			User user = create(registerCommand, userLanguage);

			// user.email=((registerCommand.email)?.toLowerCase()).trim();

			if (registerCommand.openId != null) {
				log.debug("Is an openId registration");
				/*
				 * user.accountLocked = false; user.addToOpenIds(url:
				 * command.openId); user.password = "openIdPassword"
				 * 
				 * SUserService.save(user);
				 * 
				 * if(command.facebookUser) { log.debug
				 * "registering facebook user" def token =
				 * session["LAST_FACEBOOK_USER"]
				 * facebookAuthService.registerFacebookUser token, user } else {
				 * SUserService.assignRoles(user); }
				 */
			} else {
				log.debug("Is an local account registration");
				user.setAccountLocked(true);
				userService.save(user);
			}

		    /*TODO : if(params.webaddress) {
            UserGroup userGroupInstance = UserGroup.findByWebaddress(params.webaddress);
            if(userGroupInstance) {
                if(userGroupInstance.allowUsersToJoin) {
                    def founder = userGroupInstance.getFounders(1,0)[0];
                    log.debug "Adding ${user} to the group ${userGroupInstance} using founder ${founder} authorities ";
                    SpringSecurityUtils.doWithAuth(founder.email, {
                        if(userGroupInstance.addMember(user)) {
                            flash.message = messageSource.getMessage("userGroup.joined.to.contribution", [userGroupInstance.name] as Object[], RCU.getLocale(request));  
                        }
	                    });
	                }
	            } else {
	                log.error "Cannot find usergroup with webaddress : "+params.webaddress;
	            }
	        }
	
	
	        def userProfileUrl = generateLink("SUser", "show", ["id": user.id], request)
	        activityFeedService.addActivityFeed(user, user, user, activityFeedService.USER_REGISTERED);
	        SUserService.sendNotificationMail(SUserService.NEW_USER, user, request, userProfileUrl);
	        */
			if (registerCommand.openId != null) {
				// authenticateAndRedirect user.email
			} else {
				registerAndEmail(user, request);
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
			return Response.ok(result).build();
		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		}
	}

	private User create(RegisterCommand registerCommand, Language userLanguage) {
		User user = new User();
		try {
			BeanUtils.copyProperties(user, registerCommand);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return user;
	}

	protected RegistrationCode registerAndEmail(User user, HttpServletRequest request) {
		RegistrationCode registrationCode = userService.register(user.getEmail());
		if (registrationCode != null) {
			Map<String, String> linkParams = new HashMap<String, String>();
			linkParams.put("t", registrationCode.getToken());
			String url = Utils.generateLink("register", "verifyRegistration", linkParams, request);
			sendVerificationMail(user.getUsername(), user.getEmail(), url, request);
			return registrationCode;
		}
		return null;
	}

	protected void sendVerificationMail(String username, String email, String url, HttpServletRequest request) {
		String domain = Utils.getDomainName(request);

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", Utils.capitalize(username));
		params.put("url", url);
		params.put("domain", domain);

		String body = messageService.getMessage("register.emailBody", params);

		String sub = messageService.getMessage("register.emailSubject", params);
		try {
			mailService.sendMail(email, sub, body);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	private class RegisterCommand {
		public String username;
		public String email;
		public String password;
		public String password2;
		public String name;
		public String website;
		public float timezone = 0;
		public String aboutMe;
		public String location;
		public String profilePic;
		public String openId;
		public boolean facebookUser;
		public String sexType;
		public String occupationType;
		public String institutionType;
		public double latitude;
		public double longitude;
		// String g_recaptcha_response;
		// String recaptcha_response_field;
		// String recaptcha_challenge_field;
		/*
		 * String captcha_response;
		 * 
		 * def jcaptchaService; // def recaptchaService;
		 * 
		 * static constraints= { email email: true, blank: false, nullable:
		 * false, validator: { value, command -> if (value) { def User =
		 * command.grailsApplication.getDomainClass(
		 * SpringSecurityUtils.securityConfig.userLookup.userDomainClassName).
		 * clazz if (User.findByEmail((value.toLowerCase()).trim())) { return
		 * 'registerCommand.email.unique' } } } password blank: false, nullable:
		 * false, validator: RegisterController.myPasswordValidator location
		 * blank:false, nullable:false, validator :
		 * RegisterController.locationValidator latitude blank:false,
		 * nullable:false, validator : RegisterController.latitudeValidator
		 * longitude blank:false, nullable:false, validator :
		 * RegisterController.longitudeValidator password2 validator:
		 * RegisterController.password2Validator captcha_response blank:false,
		 * nullable:false, validator: { value, command -> def session =
		 * RCH.requestAttributes.session def request =
		 * RCH.requestAttributes.request try{ if
		 * (!command.jcaptchaService.validateResponse("imageCaptcha",
		 * session.id, command.captcha_response)) {
		 * //if(!command.recaptchaService.verifyAnswer(session,
		 * request.getRemoteAddr(),
		 * ['g-recaptcha-response':command.g_recaptcha_response])) { return
		 * 'reCaptcha.invalid.message' } }catch (Exception e) { // TODO: handle
		 * exception e.printStackTrace() return 'reCaptcha.invalid.message' } }
		 * }
		 * 
		 */

	}
}
