package biodiv;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceInterceptor implements MethodInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(ResourceInterceptor.class);
	
	@Inject
	private SessionFactory sessionFactory;

	ResourceInterceptor() {
		log.debug("ResourceInterceptor constructor");
	}
	
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
    	log.debug("ResourceInterceptor methodInvocation : "+methodInvocation.getMethod().getName());
    	Object result = null;
    	try{
    		log.debug("Checking if there is a active transaction");
    		boolean isActive = (sessionFactory.getCurrentSession().getTransaction() != null) ? sessionFactory.getCurrentSession().getTransaction().isActive() : false; 
    		if ( !isActive) {  
                log.debug("Starting a new database transaction");  
                sessionFactory.getCurrentSession().beginTransaction();  
             }  else {
            	 log.debug("Using existing database transaction");
             }
//    		log.debug(methodInvocation.proceed()
//    		        + "   ResourceInterceptor: Method \""
//    		        + methodInvocation.getMethod().getName() + "\" intercepted\n");
//    		System.out.println("inside try");
    		log.debug("Invoking the AOP service method"); 
 	
    		result = methodInvocation.proceed();
    		
    		
    		if (!isActive) {  
                log.debug("Committing the database transaction");  
                sessionFactory.getCurrentSession().getTransaction().commit();  
             }  
    		
    		return result;
    		
    	}
    	catch(Throwable e){
    		e.printStackTrace();
    		try {  
                log.warn("Trying to rollback database transaction after exception");  
                sessionFactory.getCurrentSession().getTransaction().rollback();  
            } catch (Throwable rbEx) {  
                log.error("Could not rollback transaction after exception!", rbEx);  
            }  
    		throw e;
    	}
    }
}
