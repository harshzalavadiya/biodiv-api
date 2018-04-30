package biodiv.auth.register;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

	@Inject
	private RegisterService registerService;

	/**
	 * 
	 * @param registerCommand
	 *            dummy
	 * @param request
	 *            dummy
	 * @return dummy
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@Path("/user")
	public Response register(@BeanParam RegisterCommand registerCommand, @Context HttpServletRequest request) {
		log.debug("Registering user " + registerCommand.toString());

		try {
			User user = registerService.create(registerCommand, request);
			Map<String, Object> result = new HashMap<String, Object>();
			return Response.ok(result).build();
		} catch (Exception e) {
			e.printStackTrace();
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@Path("/verifyRegistration")
	public Response verifyRegistration(@QueryParam("t") String token) {
		// TODO: if (springSecurityService.isLoggedIn()) {
		// redirect uri:request.scheme+"://"+request.serverName+
		// SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
		// return;
		// }

		Map<String, Object> result = registerService.verifyRegistration(token);
		if ((boolean) result.get("success") == true) {
			return Response.ok(result).build();
		} else {
			ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, (String) result.get("message"));
			return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();

		}
	}

}
