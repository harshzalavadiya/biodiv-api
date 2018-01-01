package biodiv.maps;

import org.apache.http.StatusLine;

/**
 * 
 * The response from map module
 */
public class MapResponse {

	private StatusLine result;
	private Object document;
	
	public MapResponse(StatusLine result, Object document) {
		super();
		this.result = result;
		this.document = document;
	}
	public StatusLine getResult() {
		return result;
	}
	public void setResult(StatusLine result) {
		this.result = result;
	}
	public Object getDocument() {
		return document;
	}
	public void setDocument(Object document) {
		this.document = document;
	}

	

}