package biodiv.auth.register;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@ValidResetPasswordCommand
public class ResetPasswordCommand {
	
	@NotBlank
	@FormParam("t")
	public String token;
	
	@NotBlank
	@FormParam("password")
	@Length(min = 6)
	public String password;
	
	@NotBlank
	@FormParam("password2")
	public String password2;

	@Override
	public String toString() {
		return "ResetPasswordCommand [t=" + token + "]";
	}
	
}
