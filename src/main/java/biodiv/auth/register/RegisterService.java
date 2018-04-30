package biodiv.auth.register;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.common.MailService;
import biodiv.common.MessageService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.util.Utils;

public class RegisterService extends AbstractService<RegistrationCode> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private RegisterDao registerDao;

	@Inject
	private RegistrationCodeFactory registrationCodeFactory;

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

	@Inject
	public RegisterService(RegisterDao dao) {
		super(dao);
	}

	/**
	 * 
	 * @param registerCommand
	 *            dummy
	 * @return dummy
	 */
	User create(RegisterCommand registerCommand, HttpServletRequest request) {
/*
		   if (springSecurityService.isLoggedIn()) {
	            msg = messageSource.getMessage("login.already", null, RCU.getLocale(request))
	            def model = utilsService.getErrorModel(msg, null, OK.value());
	            withFormat {
	                json { render model as JSON }   
	                xml { render model as XML }
	            }
	            return;            
	        }   
	          
	        def conf = SpringSecurityUtils.securityConfig
	        if (command.hasErrors()) {      
	            def errors = [];   
	            for (int i = 0; i < command.errors.allErrors.size(); i++) {
	                def formattedMessage = g.message(code: command.errors.getFieldError(command.errors.allErrors.get(i).field).code)
	                errors << [field: command.errors.allErrors.get(i).field, message: formattedMessage]
	            }
	            msg = messageSource.getMessage("register.fail.follow.errors", [errors] as Object[], RCU.getLocale(request))
	            def model = utilsService.getErrorModel(msg, null, OK.value(), errors);
	            withFormat {
	                json { render model as JSON }
	                xml { render model as XML }
	            }
	            return
	        }
*/
		
		Language userLanguage = languageService.getCurrentLanguage(request);

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
		// try {
		// BeanUtils.copyProperties(user, registerCommand);
		// } catch (IllegalAccessException | InvocationTargetException e) {
		// e.printStackTrace();
		// }

		if (registerCommand.openId != null) {
			log.debug("Is an openId registration");
			/*
			 * user.accountLocked = false; user.addToOpenIds(url:
			 * command.openId); user.password = "openIdPassword"
			 * 
			 * SUserService.save(user);
			 * 
			 * if(command.facebookUser) { log.debug "registering facebook user"
			 * def token = session["LAST_FACEBOOK_USER"]
			 * facebookAuthService.registerFacebookUser token, user } else {
			 * SUserService.assignRoles(user); }
			 */
		} else {
			log.debug("Is an local account registration");
			user.setAccountLocked(true);
			userService.save(user);
		}
		
/*		   if (user == null || user.hasErrors()) {
	            def errors = [];
	            if(user) {
	                for (int i = 0; i < user.errors.allErrors.size(); i++) {
	                    def formattedMessage = g.message(code: command.errors.getFieldError(command.errors.allErrors.get(i).field).code)
	                    errors << [field: command.errors.allErrors.get(i).field, message: formattedMessage]
	                }
	            } else {
	                errors << messageSource.getMessage("user.null", null, RCU.getLocale(request))
	            }

	            msg = messageSource.getMessage("register.fail.follow.errors", [errors] as Object[], RCU.getLocale(request))

	            def model = utilsService.getErrorModel(msg, null, OK.value(), errors);
	            withFormat {
	                json { render model as JSON }
	                xml { render model as XML }
	            }
	            return
	        }

		  def userProfileUrl = generateLink("SUser", "show", ["id": user.id], request)
			        activityFeedService.addActivityFeed(user, user, user, activityFeedService.USER_REGISTERED);
			        SUserService.sendNotificationMail(SUserService.NEW_USER, user, request, userProfileUrl);
*/
		RegistrationCode registrationCode = registerAndEmail(user, request);

/*			        if (registrationCode == null || registrationCode.hasErrors()) {
			            msg = messageSource.getMessage("register.errors.send.verification.token", [user] as Object[], RCU.getLocale(request))
			                def model = utilsService.getErrorModel(msg, null, OK.value());
			            withFormat {
			                json { render model as JSON }
			                xml { render model as XML }
			            }
			        }
			        msg = messageSource.getMessage("register.success.send.verification.token", [user,user.email] as Object[], RCU.getLocale(request))
			            def model = utilsService.getSuccessModel(msg, null, OK.value());
			        withFormat {
			            json { render model as JSON }
			            xml { render model as XML }
			        }
*/
		return user;
	}

	RegistrationCode register(String email) {
		if (email == null)
			return null;
		try {
			log.info("Generating registration code for the user {} ", email);
			RegistrationCode registrationCode = registrationCodeFactory.create(email);
			if (registrationCode.save() == null) {
				log.error("Coudn't save registrationCode");
			} else {
				return registrationCode;
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	RegistrationCode registerAndEmail(User user, HttpServletRequest request) {

		RegistrationCode registrationCode = register(user.getEmail());

		log.debug("Got {} ", registrationCode);

		if (registrationCode != null) {
			Map<String, String> linkParams = new HashMap<String, String>();
			linkParams.put("t", registrationCode.getToken());
			String url;
			try {
				url = Utils.generateLink("register", "verifyRegistration", linkParams, request);
				log.debug("Sending verification email with registrationCode : {} ", url);
				sendVerificationMail(user.getUsername(), user.getEmail(), url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return registrationCode;
		}

		return null;
	}

	void sendVerificationMail(String username, String email, String url) {
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

	Map<String, Object> verifyRegistration(String token) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (token != null) {
			RegistrationCode registrationCode = registerDao.findByPropertyWithCondition("token", token, "=");

			if (registrationCode == null) {
				result.put("success", false);
				result.put("message", "Bad registration code");
				return result;
			}

			User user;

			try {
				user = userService.findByEmail(registrationCode.getUsername());
				user.setAccountLocked(false);
				userService.setDefaultRoles(user);
				userService.save(user);
				registerDao.delete(registrationCode);

				result.put("success", true);
				result.put("message", "Registration complete. Welcome!!!");

			} catch (NotFoundException e) {
				e.printStackTrace();
				result.put("success", false);
				result.put("message", "Error in verifying registration for user with email : "
						+ registrationCode.getUsername() + ".  Error Message : " + e.getMessage());
				return result;
			}

		} else {
			result.put("success", false);
			result.put("message", "Bad token");
		}
		return result;
	}

}
