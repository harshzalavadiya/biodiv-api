package biodiv.common;


import java.io.Serializable;
import java.util.Set;

import biodiv.userGroup.UserGroup;

/**
 * 
 * @author abhinav
 *
 * @param <T>
 */
public interface GenericModel<T> {

	/**
	 * 
	 * @param obvId
	 * @return
	 */
	public T get(long obvId);
		
	/**
	 * 
	 * @param obvId
	 * @return
	 */
	public T read(long obvId);
	
	/**
	 * 
	 * @param obvId
	 * @return
	 */
	public T load(long obvId);
	
	/**
	 * 
	 * @param flush
	 * @return
	 */
	public void save();
	
	/**
	 * 
	 * @param flush
	 * @return
	 */
	public void update(boolean flush);

	/**
	 * 
	 * @param obvId
	 * @return
	 */
	public void delete();
	
//    public Set<UserGroup> getUserGroups();
//    
//    public void setUserGroups(Set<UserGroup> userGroups);

}