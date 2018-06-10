package biodiv.auth.register;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidRegisterCommandValidator implements ConstraintValidator<ValidRegisterCommand, RegisterCommand> {

    @Override
    public void initialize(ValidRegisterCommand constraintAnnotation) {
    }

    @Override
    public boolean isValid(RegisterCommand object, ConstraintValidatorContext constraintValidatorContext) {
        if ( object == null ) {
            return true;
        }
        
        boolean isValid = (object.password == object.password2);

        if ( !isValid ) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate( "{user.password2.inValid}" )
                    .addPropertyNode( "password2" ).addConstraintViolation();
        }

        return isValid;
    }
}
