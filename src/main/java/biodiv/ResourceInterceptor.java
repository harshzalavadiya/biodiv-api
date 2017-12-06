package biodiv;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.util.HibernateUtil;

public class ResourceInterceptor implements MethodInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(ResourceInterceptor.class);
	
	private static SessionFactory sf = HibernateUtil.getSessionFactory();  
	
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
    	System.out.println("test");
    	Object result = null;
    	try{
    		boolean isActive = sf.getCurrentSession().getTransaction().isActive(); 
    		System.out.println(isActive);
    		if ( !isActive) {  
                log.debug("Starting a database transaction");  
                sf.getCurrentSession().beginTransaction();  
             }  
//    		log.debug(methodInvocation.proceed()
//    		        + "   ResourceInterceptor: Method \""
//    		        + methodInvocation.getMethod().getName() + "\" intercepted\n");
//    		System.out.println("inside try");
    		log.debug("Invoking the AOP service method"); 
 	
    		result = methodInvocation.proceed();
    		
    		
    		if (!isActive) {  
                log.debug("Committing the database transaction");  
                sf.getCurrentSession().getTransaction().commit();  
             }  
    		
    		return result;
    		
    	}
    	catch(Throwable e){
    		e.printStackTrace();
    		try {  
                log.warn("Trying to rollback database transaction after exception");  
                sf.getCurrentSession().getTransaction().rollback();  
            } catch (Throwable rbEx) {  
                log.error("Could not rollback transaction after exception!", rbEx);  
            }  
    	}
     return result;
    }
}
