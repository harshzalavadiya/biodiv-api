package biodiv.auth.token;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import biodiv.user.User;

@Entity
@Table(name = "token", uniqueConstraints = { @UniqueConstraint(columnNames = "id"),
		@UniqueConstraint(columnNames = { "type", "user_id" }) })
public class Token implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TokenType {
		ACCESS("Access"), REFRESH("Refresh");
		private String value;

		TokenType(String value) {
			this.value = value;
		}

		String value() {
			return this.value;
		}
	}

	private Long id;
	private String value;
	private TokenType type;
	private User user;
	private Date createdOn;

	public Token() {
		// this form is used by hibernate
	}

	public Token(String value, TokenType type, User user) {
		this.value = value;
		this.type = type;
		this.user = user;
		this.createdOn = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_generator")
	@SequenceGenerator(name = "hibernate_generator", sequenceName = "hibernate_sequence")
	@Column(name = "id", unique = true, updatable = false, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "value", nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "type", nullable = false)
	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type != other.type)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Token [id=" + id + ", value=" + value + ", type=" + type + ", user=" + user + ", createdOn=" + createdOn
				+ "]";
	}

}
