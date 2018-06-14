package biodiv.auth.register;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@ValidRegisterCommand
public class RegisterCommand {
	
	@NotBlank
	@FormParam("email")
	@Email
	@UniqueEmail
	public String email;
	
	@NotBlank
	@FormParam("password")
	@Length(min = 6)
	//blank: false, nullable: false, validator: RegisterController.myPasswordValidator
	public String password;
	
	@NotBlank
	@FormParam("password2")
	//@ValidPassword
	public String password2;
	
	@NotBlank
	@FormParam("name")
	//@Min(value = 4, message = "{user.name.size.inValid}")
	//blank: false, nullable: false,
	public String name;
	
//	@FormParam("website")
//	public String website;
	
//	public float timezone = 0;
	
//	@FormParam("aboutMe")
//	public String aboutMe;
	
	@NotBlank
	@FormParam("location")
	public String location;
	
	@NotNull
	@FormParam("latitude")
	@DecimalMin(value="-90", inclusive=true, message="user.latitude.min")
	@DecimalMax(value="90", inclusive=true, message="user.latitude.max")
	public double latitude;
	
	@NotNull
	@FormParam("longitude")
	@DecimalMin(value="-180", inclusive=true, message="user.longitude.min")
	@DecimalMax(value="180", inclusive=true, message="user.longitude.max")
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
		return "RegisterCommand [email=" + email + ", password=XXXX, password2=XXXX" + ", name="
				+ name + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", profilePic=" + profilePic + ", openId=" + openId + ", facebookUser=" + facebookUser + ", sexType="
				+ sexType + ", occupationType=" + occupationType + ", institutionType=" + institutionType + "]";
	}
	
}
