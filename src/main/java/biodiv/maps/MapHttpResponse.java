package biodiv.maps;

import org.apache.http.StatusLine;

/**
 * 
 * Http response code and message received.
 */
public class MapHttpResponse {

	private StatusLine statusCode;

	private Object document;

	public MapHttpResponse(StatusLine statusCode, Object document) {
		super();
		this.statusCode = statusCode;
		this.document = document;
	}

	public StatusLine getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(StatusLine statusCode) {
		this.statusCode = statusCode;
	}

	public Object getDocument() {
		return document;
	}

	public void setDocument(Object document) {
		this.document = document;
	}

	

}
