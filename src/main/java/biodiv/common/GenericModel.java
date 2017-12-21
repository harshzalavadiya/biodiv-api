package biodiv.common;


import java.io.Serializable;

import java.util.Set;

import biodiv.userGroup.UserGroup;


/**
 * 
 * @author 
 * dummy
 *
 * @param <T>
 * dummy
 */
public interface GenericModel<T> {

	/**
	 * 
	 * @param obvId
	 * dummy
	 * @return
	 * dummy
	 */
	public T get(long obvId);
		
	/**
	 * 
	 * @param obvId
	 * dummy
	 * @return
	 * dummy
	 */
	public T read(long obvId);
	
	/**
	 * 
	 * @param obvId
	 * dummy
	 * @return
	 * dummy
	 */
	public T load(long obvId);
	
	/**
	 * 
	 */
	public void save();
	/**
	 * 
	 * @param flush
	 * dummy
	 */
	public void update(boolean flush);

	
	
	public void delete();


}