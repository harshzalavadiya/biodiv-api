package biodiv.auth.register;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.ws.rs.NotFoundException;

import biodiv.user.UserService;
import biodiv.user.User;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	@Inject
	private UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if ( object == null ) {
            return true;
        }
        User user = null;
        try {
        	user = userService.findByEmail(object);
        	if(user == null) return true;
        } catch(NotFoundException e) {
        	return true;
        }
       	return false;
    }
}
