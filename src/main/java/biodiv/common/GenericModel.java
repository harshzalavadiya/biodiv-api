package biodiv.common;

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
	 * @param id
	 * dummy
	 * @return
	 * dummy
	 */
	public T get(long id);
	//TODO:HACK to pass service as injection was not possible in domain objects
	public T get(long id, AbstractService<T> abstractService);
		
	/**
	 * 
	 * @param id
	 * dummy
	 * @return
	 * dummy
	 */
	public T read(long id);
	
	/**
	 * 
	 * @param id
	 * dummy
	 * @return
	 * dummy
	 */
	public T load(long id);
	
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