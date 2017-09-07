package biodiv.auth.token;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import biodiv.user.User;

@Entity
@Table(name = "token", uniqueConstraints = { @UniqueConstraint(columnNames = "id"), @UniqueConstraint(columnNames = "type,user") })
public class Token {

    public enum TokenType {
        ACCESS("Access"),
        REFRESH("Refresh");
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
    private Integer userId;
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

	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name = "type")
	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	@GenericGenerator(name = "generator", strategy = "foreign",
			parameters = @Parameter(name = "property", value = "user"))
			@Id
			@GeneratedValue(generator = "generator")
			@Column(name = "user_id", unique = true, nullable = false)
			public Integer getUserId() {
				return this.userId;
			}

			public void setUserId(Integer userId) {
				this.userId = userId;
			}
			
	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Token [id=" + id + ", value=" + value + ", type=" + type + ", user=" + user + ", createdOn=" + createdOn
				+ "]";
	}
    
}
