package biodiv.auth.register;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.common.MailService;
import biodiv.common.MessageService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.util.Utils;

@Path("/register")
public class RegisterController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private UserService userService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private MessageService messageService;
	
	@Inject
	private MailService mailService;
	
	@Inject
	Configuration config;
	
	/**
	 * 
	 * @param registerCommand
	 * dummy
	 * @param request
	 * dummy
	 * @return
	 * dummy
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
    @Path("/user")
	public Response register(@BeanParam RegisterCommand registerCommand, @Context HttpServletRequest request) {
		log.debug("Registering user " + registerCommand.toString());

		try {
			Language userLanguage = languageService.getCurrentLanguage(request);

			User user = create(registerCommand, userLanguage);

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

			/*
			 * TODO : if(params.webaddress) { UserGroup userGroupInstance =
			 * UserGroup.findByWebaddress(params.webaddress);
			 * if(userGroupInstance) { if(userGroupInstance.allowUsersToJoin) {
			 * def founder = userGroupInstance.getFounders(1,0)[0]; log.debug
			 * "Adding ${user} to the group ${userGroupInstance} using founder ${founder} authorities "
			 * ; SpringSecurityUtils.doWithAuth(founder.email, {
			 * if(userGroupInstance.addMember(user)) { flash.message =
			 * messageSource.getMessage("userGroup.joined.to.contribution",
			 * [userGroupInstance.name] as Object[], RCU.getLocale(request)); }
			 * }); } } else { log.error
			 * "Cannot find usergroup with webaddress : "+params.webaddress; } }
			 * 
			 * 
			 * def userProfileUrl = generateLink("SUser", "show", ["id":
			 * user.id], request) activityFeedService.addActivityFeed(user,
			 * user, user, activityFeedService.USER_REGISTERED);
			 * SUserService.sendNotificationMail(SUserService.NEW_USER, user,
			 * request, userProfileUrl);
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
	/**
	 * 
	 * @param registerCommand
	 * dummy
	 * @param userLanguage
	 * dummy
	 * @return
	 * dummy
	 */
	private User create(RegisterCommand registerCommand, Language userLanguage) {
		User user = new User();
		user.setEmail(registerCommand.email.toLowerCase().trim());
		user.setName(registerCommand.name);
		user.setUsername(registerCommand.name);
		user.setPassword(registerCommand.password);
		user.setLocation(registerCommand.location);
		user.setLatitude(registerCommand.latitude);
		user.setLongitude(registerCommand.longitude);
		user.setSexType(registerCommand.sexType);
		user.setOccupationType(registerCommand.occupationType);
		user.setInstitutionType(registerCommand.institutionType);
		user.setProfilePic(registerCommand.profilePic);
		user.setLanguage(userLanguage);
		user.setEnabled(true);
//		try {
//			BeanUtils.copyProperties(user, registerCommand);
//		} catch (IllegalAccessException | InvocationTargetException e) {
//			e.printStackTrace();
//		}
		return user;
	}

	protected RegistrationCode registerAndEmail(User user, HttpServletRequest request) {
		RegistrationCode registrationCode = userService.register(user.getEmail());
		log.debug("Got {} ", registrationCode);
		if (registrationCode != null) {
			Map<String, String> linkParams = new HashMap<String, String>();
			linkParams.put("t", registrationCode.getToken());
			String url;
			try {
				url = Utils.generateLink("register", "verifyRegistration", linkParams, request);
				log.debug("Sending verification email with registrationCode : {} ", url);
				sendVerificationMail(user.getUsername(), user.getEmail(), url, request);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return registrationCode;
		}
		return null;
	}

	protected void sendVerificationMail(String username, String email, String url, HttpServletRequest request) {
		String domain = config.getString("siteName");

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", Utils.capitalize(username));
		params.put("url", url);
		params.put("domain", domain);
		
		String sub = messageService.getMessage("register.emailSubject", params);
		log.debug(sub);
		String body = messageService.getMessage("register.emailBody", params);
		log.debug(body);
		
		try {
			mailService.sendMail(email, sub, body);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

}
