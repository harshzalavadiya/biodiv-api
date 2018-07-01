package biodiv.auth.register;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.ws.rs.NotFoundException;

import org.hibernate.SessionFactory;

import biodiv.user.UserService;
import biodiv.Transactional;
import biodiv.user.User;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	@Inject
	private UserService userService;

	//@Inject
	//private SessionFactory sessionFactory;
	
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
        	//sessionFactory.getCurrentSession().beginTransaction();
        	user = userService.findByEmail(object);
        	//sessionFactory.getCurrentSession().getTransaction().commit();;
        	if(user == null) return true;
        } catch(NotFoundException e) {
        	return true;
        }
       	return false;
    }
}
