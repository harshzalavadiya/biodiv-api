package biodiv.common;


import java.io.Serializable;

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
	public T save(boolean flush);
	
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

}