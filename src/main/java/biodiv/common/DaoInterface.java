package biodiv.common;

import java.io.Serializable;
import java.util.List;

public interface DaoInterface <T, Id extends Serializable> {

	public void save(T entity);
	
	public void update(T entity);
	
	public void delete(T entity);
	
	public T findById(Id id);
	
	public List<T> findAll();
	
}
