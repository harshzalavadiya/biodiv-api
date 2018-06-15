package biodiv.auth.register;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.LanguageService;
import biodiv.common.MailService;
import biodiv.common.MessageService;
import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.user.UserService;

@Path("/register")
public class RegisterController {

	private final Logger log = LoggerFactory.getLogger(getClass());

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
	public Response register(@Valid @BeanParam RegisterCommand registerCommand, @Context HttpServletRequest request) {
		log.debug("Registering user " + registerCommand.toString());

		GoogleRecaptchaCheck recaptcha = new GoogleRecaptchaCheck(registerCommand.recaptchaResponse);
		try {
			if (recaptcha.isRobot()) {
				return Response.status(HttpStatus.SC_FORBIDDEN).build();
			} else {
				try {
					User user = registerService.create(registerCommand, request);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("success", true);
					result.put("msg",
							"Welcome. An activation email has been sent to your email. Please click the confirmation link to activate your account.");
					return Response.ok(result).entity(result).build();
				} catch (Exception e) {
					e.printStackTrace();
					ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, e.getMessage());
					return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return Response.serverError().build();
		}

	}

	@GET
	//@Produces(MediaType.APPLICATION_JSON)
	@Path("/verifyRegistration")
	public void verifyRegistration(@QueryParam("t") String token) {
		// TODO: if (springSecurityService.isLoggedIn()) {
		// redirect uri:request.scheme+"://"+request.serverName+
		// SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
		// return;
		// }
		log.debug("Verifying registration code : " + token);

		Map<String, Object> result = registerService.verifyRegistration(token);
		log.debug(result.toString());
		if ((boolean) result.get("success") == true) {
			try {
				Response.temporaryRedirect(new URI(config.getString("serverUrl")));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			//return Response.ok(result).build();
		} else {
			//ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, (String) result.get("message"));
			//return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			try {
				Response.temporaryRedirect(new URI(config.getString("serverUrl")));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		}
	}

}
