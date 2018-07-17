package biodiv.auth.register;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import biodiv.common.AbstractObject;
import biodiv.common.CommonMethod;

@Entity
@Table(name = "registration_code")
public class RegistrationCode extends AbstractObject {
	
	private long id;
	private Date dateCreated;
	private String token;
	private String username;

	RegistrationCode() {
		this.dateCreated = new Date();
		this.token = UUID.randomUUID().toString().replaceAll("-", "");
		this.username = username;
	}
	
	//@AssistedInject
	public RegistrationCode(/*SessionFactory sessionFactory, @Assisted*/ String username) {
		//super(sessionFactory);
		this.dateCreated = new Date();
		this.token = UUID.randomUUID().toString().replaceAll("-", "");
		this.username = username;
	}

	@Id
	@GenericGenerator(
	        name = "hibernate_generator",
	        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	        parameters = {
	                @Parameter(name = "sequence_name", value = "hibernate_sequence"),
	                @Parameter(name = "increment_size", value = "1"),
                    @Parameter(name = "optimizer", value = "hilo")
	        }
	)
	@GeneratedValue(generator = "hibernate_generator")
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 29)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "token", nullable = false)
	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column(name = "username", nullable = false)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "RegistrationCode [id=" + id + ", dateCreated=" + dateCreated + ", token=" + token + ", username="
				+ username + "]";
	}

}
