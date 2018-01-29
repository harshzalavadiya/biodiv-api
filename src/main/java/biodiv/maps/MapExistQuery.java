package biodiv.maps;

public class MapExistQuery {
	private String key;
    private boolean exists;
    
    
    
	public MapExistQuery(String key, boolean exists) {
		super();
		this.key = key;
		this.exists = exists;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isExists() {
		return exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
    
}
