package biodiv.common;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.util.HibernateUtil;

public class CommonMethod<T> implements GenericModel {
	
	private static final Logger log = LoggerFactory.getLogger(DataObject.class);
	@Override
	public T get(long Id) {
		// TODO Auto-generated method stub
		//	Class<? extends CommonMethod> entity=this.getClass();
		//System.out.println(entity);
		T instance = (T) HibernateUtil.getSessionFactory().getCurrentSession().get(this.getClass(), Id);
		return instance;
	}

	@Override
	public T read(long Id) {
		// TODO Auto-generated method stub
		T instance = (T) HibernateUtil.getSessionFactory().getCurrentSession().load(this.getClass(), Id);
		
		return instance;
		
	}

	@Override
	public Object load(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void update(boolean flush) {
		// TODO Auto-generated method stub
		System.out.println("Object to update : " + this);
		try{
			HibernateUtil.getSessionFactory().getCurrentSession().update(this);
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
			HibernateUtil.getSessionFactory().getCurrentSession().delete(this);
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
			HibernateUtil.getSessionFactory().getCurrentSession().save(this);
			log.debug("save successful");
		}catch(RuntimeException re){
			log.error("save  failed", re);
			throw re;
		}
		
	}

}
