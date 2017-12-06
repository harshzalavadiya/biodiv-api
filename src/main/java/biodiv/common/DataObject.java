package biodiv.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.MappedSuperclass;

import biodiv.util.HibernateUtil;

@MappedSuperclass

public class DataObject<T>  extends ParticipationMetadata {
	
	
	
	
	
	public Class<T> entityClass;

	public DataObject() {
		
		
		
	}
	@Override
	public T get(long Id) {
		//System.out.println("DataObject class: " + this.getClass());
		T instance = (T) HibernateUtil.getSessionFactory().getCurrentSession().get(this.getClass(), Id);
		return instance;
	}

	@Override
	public T read(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T load(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(boolean flush) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(boolean flush) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}
}
