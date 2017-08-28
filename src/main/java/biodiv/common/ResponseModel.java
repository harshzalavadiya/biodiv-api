package biodiv.common;

import javax.ws.rs.core.Response;

public class ResponseModel {

	String status;
	String message;
	Object object;
	
	public ResponseModel(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public ResponseModel(String status, String message, Object object) {
		this.status = status;
		this.message = message;
		this.object = object;
	}
	
	public ResponseModel(Response.Status status, String message) {
		this.status = status.getStatusCode()+"";
		this.message = message;
	}

	public ResponseModel(Response.Status status, String message, Object object) {
		this.status = status.getStatusCode()+"";
		this.message = message;
		this.object = object;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "ResponseModel [status=" + status + ", message=" + message + ", object=" + object + "]";
	}
}
