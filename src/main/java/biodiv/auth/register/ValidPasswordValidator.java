package biodiv.auth.register;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    //private String email;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        //this.email = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if ( object == null ) {
            return true;
        }
        //TODO: check if a user already exists with this email
        return false;
    }
}
