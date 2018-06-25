package biodiv.auth.register;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpStatus;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.ResponseModel;
import biodiv.user.User;
import biodiv.util.Utils;

@Path("/register")
public class RegisterController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	Configuration config;

	@Inject
	private RegisterService registerService;
	
	@Inject
	private GoogleRecaptchaCheck googleRecaptchaCheck;
	

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
	public Response register(@Valid @BeanParam RegisterCommand registerCommand, @FormParam("webaddress") String webaddress, @Context HttpServletRequest request, @Pac4JProfile Optional<CommonProfile> profile, @Pac4JProfileManager ProfileManager<CommonProfile> profileM) {
		System.out.println(profileM);
		System.out.println(profileM.get(true));
		
		System.out.println(profile);
		if (profile.isPresent()) {
			ResponseModel responseModel = new ResponseModel(Response.Status.BAD_REQUEST, "Please logout before registering.");
			return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } 
		
		log.debug("Registering user " + registerCommand.toString());

		try {
			if (googleRecaptchaCheck.isRobot(registerCommand.recaptchaResponse)) {
				return Response.status(HttpStatus.SC_FORBIDDEN).build();
			} else {
				try {
					User user = registerService.create(registerCommand, webaddress, request);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("success", true);
					String msg = "Welcome !!!";
					if(registerCommand.openId == null) {
						msg += "An activation email has been sent to your email. Please click the confirmation link to activate your account.";
					}
					result.put("msg", msg);
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
	public Response verifyRegistration(@QueryParam("t") String token, @Context HttpServletRequest request, @Pac4JProfile Optional<CommonProfile> profile) {
		if (profile.isPresent()) {
			throw new WebApplicationException(400);
        } 
		log.debug("Verifying registration code : " + token);

		Map<String, Object> result = registerService.verifyRegistration(token, request);
		log.debug(result.toString());
		URI url = null;
		
		try {
			url = new URI(config.getString("serverUrl"));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			url = null;
		}
		
		if ((boolean) result.get("success") == true) {
			try {
				String contextPath = request.getContextPath();
				url = new URI(Utils.generateLink("login", "auth", new HashMap(), request).replace(contextPath, ""));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			//return Response.ok(result).build();
			return Response.temporaryRedirect(url).build();
		} else {
			//ResponseModel responseModel = new ResponseModel(Response.Status.FORBIDDEN, (String) result.get("message"));
			//return Response.status(Response.Status.FORBIDDEN).entity(responseModel).build();
			return Response.temporaryRedirect(url).build();
		}
	}

}
