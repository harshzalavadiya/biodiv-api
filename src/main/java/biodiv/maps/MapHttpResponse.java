package biodiv.maps;

/**
 * 
 * Http response code and message received.
 */
public class MapHttpResponse {

	private int statusCode;
	
	private String message;
	
	public MapHttpResponse(int statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
