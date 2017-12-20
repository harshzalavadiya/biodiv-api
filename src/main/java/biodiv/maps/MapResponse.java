package biodiv.maps;

import org.elasticsearch.action.DocWriteResponse.Result;

/**
 * 
 * The response from map module
 */
public class MapResponse {

	private Result result;
	
	private String message;

	public MapResponse (Result result, String message) {
		super();
		this.result = result;
		this.message = message;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
