package biodiv.auth;

import java.util.Map;

public class AccountNotFoundException extends org.pac4j.core.exception.AccountNotFoundException {

	Map<String, String> details;
	
	public AccountNotFoundException(String message, Map<String, String> details) {
		super(message);
		this.details = details;
	}
	
	public Map<String, String> getDetails() {
		return details;
	}

}
