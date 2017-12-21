package biodiv.maps;

/**
 * 
 * The response from map module
 */
public class MapResponse {

	private int result;
	
	private String message;

	public MapResponse (int result, String message) {
		super();
		this.result = result;
		this.message = message;
	}

	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}



	public int getResult() {
		return result;
	}



	public void setResult(int result) {
		this.result = result;
	}
	
}
