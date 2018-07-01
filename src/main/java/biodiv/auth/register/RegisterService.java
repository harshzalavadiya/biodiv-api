package biodiv.auth.register;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.mail.HtmlEmail;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.Transactional;
import biodiv.activityFeed.ActivityFeedService;
import biodiv.auth.MessageDigestPasswordEncoder;
import biodiv.common.AbstractService;
import biodiv.common.Language;
import biodiv.common.LanguageService;
import biodiv.common.MessageService;
import biodiv.user.User;
import biodiv.user.UserService;
import biodiv.userGroup.UserGroupService;
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
	Configuration config;

	@Inject
	private SessionFactory sessionFactory;

	@Inject
	private ActivityFeedService activityFeedService;

	@Inject
	private RegisterMailingService registerMailingService;

	@Inject
	private UserGroupService userGroupService;

	@Inject
	public RegisterService(RegisterDao registerDao) {
		super(registerDao);
		this.registerDao = registerDao;
		System.out.println("RegisterService constructor");
	}

	/**
	 * 
	 * @param registerCommand
	 *            dummy
	 * @return dummy
	 */
	User create(RegisterCommand registerCommand, String webaddress, HttpServletRequest request) {
		/*
		 * if (springSecurityService.isLoggedIn()) { msg =
		 * messageSource.getMessage("login.already", null,
		 * RCU.getLocale(request)) def model = utilsService.getErrorModel(msg,
		 * null, OK.value()); withFormat { json { render model as JSON } xml {
		 * render model as XML } } return; }
		 * 
		 * 
		 */

		Language userLanguage = languageService.getCurrentLanguage(request);

		User user = new User();
		user.setEmail(registerCommand.email.toLowerCase().trim());
		user.setName(registerCommand.name);
		user.setUsername(registerCommand.name);
		MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");
		user.setPassword(passwordEncoder.encodePassword(registerCommand.password, null));
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
			// TODO: verify tht openId is valid openId to unlock user accounte
			userService.setDefaultRoles(user);
			user.setAccountLocked(false);
		} else {
			log.debug("Is an local account registration");
			user.setAccountLocked(true);
		}

		try {
			userService.save(user);
			userGroupService.addMember(webaddress, user);

			String activityDescription = "";
			Long activityHolderId = user.getId();
			Date dateCreated = new java.util.Date();
			Date lastUpdated = dateCreated;
			Map<String, Object> afNew = activityFeedService.createMapforAf("Object", user.getId(), user,
					"species.auth.SUser", "species.auth.SUser", user.getId(), "Registered to portal",
					"Registered to portal", activityDescription, activityDescription, null, null, null, true, null,
					dateCreated, lastUpdated);
			activityFeedService.addActivityFeed(user, afNew, null, (String) afNew.get("rootHolderType"));

			// SUserService.sendNotificationMail(SUserService.NEW_USER, user,
			// request, userProfileUrl);
			if (registerCommand.openId != null) {
				sendWelcomeMail(user, request);
			}

		} catch (Exception re) {
			log.error("persist failed for user", re);
			user = null;
			throw re;
		}

		if (user != null) {
			RegistrationCode registrationCode = registerAndEmail(user, request);
		}
		return user;
	}

	@Transactional
	private RegistrationCode registerAndEmail(User user, HttpServletRequest request) {

		RegistrationCode registrationCode = register(user.getEmail());

		log.debug("Got {} ", registrationCode);

		if (registrationCode != null) {
			Map<String, String> linkParams = new HashMap<String, String>();
			linkParams.put("t", registrationCode.getToken());
			String url;
			try {
				url = Utils.generateLink("register", "verifyRegistration", linkParams, request, false);
				log.debug("Sending verification email with registrationCode : {} ", url);
				sendVerificationMail(user.getUsername(), user.getEmail(), url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return registrationCode;
		}

		return null;
	}

	private RegistrationCode register(String email) {
		if (email == null)
			return null;
		try {
			log.info("Generating registration code for the user {} ", email);
			RegistrationCode registrationCode = registrationCodeFactory.create(email);
			if (registrationCode.save(sessionFactory) == null) {
				log.error("Coudn't save registrationCode");
			} else {
				return registrationCode;
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	private void sendVerificationMail(String username, String email, String url) {
		String domain = config.getString("siteName");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", Utils.capitalize(username));
		params.put("url", url);
		params.put("domain", domain);

		try {
			// mailService.sendMail(email, sub, body);
			List<User> allBccs = registerMailingService.getAllBccPeople();
			for (User bcc : allBccs) {
				HtmlEmail emailToBcc = registerMailingService.buildActivationMailMessage(bcc.getEmail(), params);
			}

			HtmlEmail emailToPostingUser = registerMailingService.buildActivationMailMessage(email, params);

			if (!registerMailingService.isAnyThreadActive()) {
				System.out.println("no thread is active currently");
				Thread th = new Thread(registerMailingService);
				th.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	private void sendWelcomeMail(User user, HttpServletRequest request) {
		String domain = config.getString("siteName");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", Utils.capitalize(user.getUsername()));
		params.put("domain", domain);
		params.put("serverUrl", config.getString("serverUrl"));
		params.put("siteName", config.getString("siteName"));
		params.put("facebookUrl", config.getString("facebookUrl"));
		params.put("twitterUrl", config.getString("twitterUrl"));
		params.put("feedbackFormUrl", config.getString("feedbackFormUrl"));
		params.put("mailDefaultFrom", config.getString("mail.senderEmail"));
		params.put("welcomeEmailIntro", messageService.getMessage("welcomeEmail.intro"));
		params.put("welcomeEmailObservation", messageService.getMessage("welcomeEmail.observation"));
		params.put("welcomeEmailMap", messageService.getMessage("welcomeEmail.map"));
		params.put("welcomeEmailChecklist", messageService.getMessage("welcomeEmail.checklist"));
		params.put("welcomeEmailSpecies", messageService.getMessage("welcomeEmail.species"));
		params.put("welcomeEmailGroups", messageService.getMessage("welcomeEmail.groups"));
		params.put("welcomeEmailDocuments", messageService.getMessage("welcomeEmail.documents"));

		Map<String, String> linkParams = new HashMap<String, String>();
		linkParams.put("id", String.valueOf(user.getId()));
		String url;
		try {
			url = Utils.generateLink("user", "show", linkParams, request);
			params.put("userProfileUrl", Utils.generateLink("user", "show", linkParams, request));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// mailService.sendMail(email, sub, body);
			List<User> allBccs = registerMailingService.getAllBccPeople();
			for (User bcc : allBccs) {
				HtmlEmail emailToBcc = registerMailingService.buildWelcomeMailMessage(bcc.getEmail(), params);
			}

			HtmlEmail emailToPostingUser = registerMailingService.buildWelcomeMailMessage(user.getEmail(), params);

			if (!registerMailingService.isAnyThreadActive()) {
				System.out.println("no thread is active currently");
				Thread th = new Thread(registerMailingService);
				th.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	@Transactional
	Map<String, Object> verifyRegistration(String token, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		log.debug("Verifying registration code {} ", token);
		if (token != null) {
			RegistrationCode registrationCode = registerDao.findByPropertyWithCondition("token", token, "=");
			log.debug("registrationCode {}", registrationCode);
			if (registrationCode == null) {
				result.put("success", false);
				result.put("msg", "Registration code is already validated or got deleted. Please use forgot password link to reset password and reactivate account.");
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
				result.put("msg", "Registration is complete. Welcome!!! Please login to contribute.");
				sendWelcomeMail(user, request);
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				result.put("success", false);
				result.put("msg", "User account with email "+registrationCode.getUsername()+" doesnot exist. Please register.");
				return result;
			}

		} else {
			result.put("success", false);
			result.put("msg", "Bad token");
		}
		return result;
	}

	@Transactional
	Map<String, Object> forgotPassword(String email, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		log.debug("Forgot password for {} ", email);
		if(email == null || email.isEmpty()) {
			result.put("success", false);
			result.put("msg", "Please provide email address for the account for which you would like to reset password.");
			return result;
		}
		
	    email = email.toLowerCase().trim();
		
	    User user;
		
	    try {
	    	user = userService.findByEmail(email);
	    	if(user == null) {
	    		result.put("success", false);
				result.put("msg", "User account with email "+email+" doesnot exist. Please register.");
				return result;
	    	} else {
		        RegistrationCode registrationCode = register(email);
		        log.debug("Got {} ", registrationCode);

				if (registrationCode != null) {
					Map<String, String> linkParams = new HashMap<String, String>();
					linkParams.put("t", registrationCode.getToken());
					String url;
					try {
						url = Utils.generateLink("register", "resetPassword", linkParams, request, false);
						log.debug("Sending forgotpassword email with registrationCode : {} ", url);
						sendForgotPasswordMail(user.getUsername(), user.getEmail(), url);
						result.put("success", true);
						result.put("msg", "An email as been sent to your registered email account. Please click on the provided link to reset your password.");
						return result;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				result.put("success", false);
				result.put("msg", "There seems to be some problem in serving your request. We will get back to you shortly.");
				return result;
	    	}
		} catch (NotFoundException e) {
			log.error(e.getMessage());
			result.put("success", false);
			result.put("msg", "User account with email "+email+" doesnot exist. Please register.");
			return result;
		}
	}
	
	private void sendForgotPasswordMail(String username, String email, String url) {
		String domain = config.getString("siteName");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", Utils.capitalize(username));
		params.put("url", url);
		params.put("domain", domain);

		try {
			// mailService.sendMail(email, sub, body);
			List<User> allBccs = registerMailingService.getAllBccPeople();
			for (User bcc : allBccs) {
				HtmlEmail emailToBcc = registerMailingService.buildResetPasswordMailMessage(bcc.getEmail(), params);
			}

			HtmlEmail emailToPostingUser = registerMailingService.buildResetPasswordMailMessage(email, params);

			if (!registerMailingService.isAnyThreadActive()) {
				System.out.println("no thread is active currently");
				Thread th = new Thread(registerMailingService);
				th.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	   
	@Transactional
	Map<String, Object> resetPassword(ResetPasswordCommand command, HttpServletRequest request) {
		String token = command.token;
		Map<String, Object> result = new HashMap<String, Object>();
		log.debug("Verifying forgot password code {} ", token);
		if (token != null) {
			RegistrationCode registrationCode = registerDao.findByPropertyWithCondition("token", token, "=");
			log.debug("forgotPasswordCode {}", registrationCode);
			if (registrationCode == null) {
				result.put("success", false);
				result.put("msg", "Reset password code is already validated or got deleted. Please use forgot password link to reset password again.");
				return result;
			}

			User user;

			try {
				user = userService.findByEmail(registrationCode.getUsername());
				MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");
				user.setPassword(passwordEncoder.encodePassword(command.password, null));
				user.setAccountLocked(false);
				userService.setDefaultRoles(user);
				userService.save(user);
				registerDao.delete(registrationCode);

				result.put("success", true);
				result.put("msg", "Password has been reset. Please login to contribute.");
			} catch (NotFoundException e) {
				log.error(e.getMessage());
				result.put("success", false);
				result.put("msg", "User account with email "+registrationCode.getUsername()+" doesnot exist. Please register.");
				return result;
			}

		} else {
			result.put("success", false);
			result.put("msg", "Bad token");
		}
		return result;
	}

}
