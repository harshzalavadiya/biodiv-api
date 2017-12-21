package biodiv.maps;

/**
 * 
 * The response from map module
 */
public class MapResponse {

	private MapQueryStatus result;
	
	private String message;

	public MapResponse (MapQueryStatus result, String message) {
		super();
		this.result = result;
		this.message = message;
	}

	public MapQueryStatus getResult() {
		return result;
	}

	public void setResult(MapQueryStatus result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}