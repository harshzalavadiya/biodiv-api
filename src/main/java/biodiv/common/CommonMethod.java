package biodiv.common;

import java.io.Serializable;

import biodiv.util.HibernateUtil;

public class CommonMethod<T> implements GenericModel {

	@Override
	public T get(long Id) {
		// TODO Auto-generated method stub
		//	Class<? extends CommonMethod> entity=this.getClass();
		//System.out.println(entity);
		T instance = (T) HibernateUtil.getSessionFactory().getCurrentSession().get(this.getClass(), Id);
		return instance;
	}

	@Override
	public Object read(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object load(long obvId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T save(boolean flush) {
		
		T instance = (T) HibernateUtil.getSessionFactory().getCurrentSession().save(this.getClass());
		return instance;
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
