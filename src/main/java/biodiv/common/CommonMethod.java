package biodiv.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonMethod<T> implements GenericModel<T> {
	
	private static final Logger log = LoggerFactory.getLogger(CommonMethod.class);
	
	@Inject
	private SessionFactory sessionFactory;
	
	@Override
	public T get(long Id) {
		//	Class<? extends CommonMethod> entity=this.getClass();
		//System.out.println(entity);
		T instance = (T) sessionFactory.getCurrentSession().get(this.getClass(), Id);
		return instance;
	}

	@Override
	public T read(long Id) {
		T instance = (T) sessionFactory.getCurrentSession().load(this.getClass(), Id);
		return instance;
		
	}

	@Override
	public T load(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void update(boolean flush) {
		System.out.println("Object to update : " + this);
		try{
			sessionFactory.getCurrentSession().update(this);
			log.debug("update  successful");
		}catch(RuntimeException re){
			log.error("update  failed", re);
			throw re;
		}
		
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		System.out.println("Object to delete : " + this);
		try{
			sessionFactory.getCurrentSession().delete(this);
			log.debug("delete  successful");
		}catch(RuntimeException re){
			log.error("delete  failed", re);
			throw re;
		}
		
	}

	@Override
	public void save() {
		System.out.println("Object to save : " + this);
		try{
			sessionFactory.getCurrentSession().save(this);
			log.debug("save successful");
		}catch(RuntimeException re){
			log.error("save  failed", re);
			throw re;
		}
		
	}
	
	//TODO:move this to Utils and change to static
	public Set<String> cSTSOT(String str){
		if(str == null|| str== "" || str.isEmpty())
			return new HashSet<String>();
		
		String [] y = str.split(",");
		Set<String> strSet1 = Arrays.stream(y).collect(Collectors.toSet());
		return strSet1;
		
	}
	public  boolean isParsableAsLong(final String s) {
	    try {
	        Long.valueOf(s);
	        return true;
	    } catch (NumberFormatException numberFormatException) {
	        return false;
	    }
	}


	@Override
	public T get(long id, AbstractService<T> abstractService) {
		// TODO Auto-generated method stub
		
		return null;
	}

	public List<Long> getListOfIds(String str) {
		// TODO Auto-generated method stub
		if(str == null|| str== "" || str.isEmpty())
			return new ArrayList<Long>();
		String [] y = str.split(",");
		List<Long> LongIds=new ArrayList<>();
		for(String z:y){
			if(isParsableAsLong(z)){
				LongIds.add(Long.parseLong(z));
			}
			else{
				LongIds.add(0L);
			}
		}
		
		
		return LongIds;
	}

}
