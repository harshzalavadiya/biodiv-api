package biodiv.auth.register;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE_USE, ANNOTATION_TYPE })
@Retention (RUNTIME)
@Constraint(validatedBy = { ValidRegisterCommandValidator.class })
@Documented
public @interface ValidRegisterCommand {
	  String message() default "{user.register.command.invalid}";

	    Class<?>[] groups() default { };

	    Class<? extends Payload>[] payload() default { };
}
