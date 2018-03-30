package biodiv.auth.register;

import javax.ws.rs.FormParam;

public class RegisterCommand {
	
	@FormParam("email")
	public String email;
	
	@FormParam("password")
	public String password;
	
	@FormParam("password2")
	public String password2;
	
	@FormParam("name")
	public String name;
	
//	@FormParam("website")
//	public String website;
	
//	public float timezone = 0;
	
//	@FormParam("aboutMe")
//	public String aboutMe;
	
	@FormParam("location")
	public String location;
	
	@FormParam("latitude")
	public double latitude;
	
	@FormParam("longitude")
	public double longitude;
	
	@FormParam("profilePic")
	public String profilePic;
	
	@FormParam("openId")
	public String openId;
	
	@FormParam("facebookUser")
	public boolean facebookUser;
	
	@FormParam("sexType")
	public String sexType;
	
	@FormParam("occupationType")
	public String occupationType;
	
	@FormParam("institutionType")
	public String institutionType;

	@Override
	public String toString() {
		return "RegisterCommand [email=" + email + ", password=" + password + ", password2=" + password2 + ", name="
				+ name + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", profilePic=" + profilePic + ", openId=" + openId + ", facebookUser=" + facebookUser + ", sexType="
				+ sexType + ", occupationType=" + occupationType + ", institutionType=" + institutionType + "]";
	}
	
	
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
