package biodiv.auth.register;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidResetPasswordCommandValidator implements ConstraintValidator<ValidResetPasswordCommand, ResetPasswordCommand> {

    @Override
    public void initialize(ValidResetPasswordCommand constraintAnnotation) {
    }

    @Override
    public boolean isValid(ResetPasswordCommand object, ConstraintValidatorContext constraintValidatorContext) {
        if ( object == null ) {
            return true;
        }
        
        boolean isValid = (object.password.equals(object.password2));
        if ( !isValid ) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate( "{user.password2.inValid}" )
                    .addPropertyNode( "password2" ).addConstraintViolation();
        }

        return isValid;
    }
}
