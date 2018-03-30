package biodiv.auth.register;

public interface RegistrationCodeFactory {
	
	RegistrationCode create(String username);
	
}