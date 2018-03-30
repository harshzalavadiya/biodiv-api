package biodiv.user;
// Generated 31 Jul, 2017 7:18:53 AM by Hibernate Tools 3.5.0.Final

import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import biodiv.auth.token.Token;
import biodiv.common.CommonMethod;
import biodiv.common.Language;

@Entity
@Table(name = "suser", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User extends CommonMethod implements Principal {

	private long id;
	private String name;
	private String username;
	private String email;
	@JsonIgnore
	private String password;	
	private String profilePic;
	private String institutionType;
	private String occupationType;
	private String sexType;
	private String location;
	private Double latitude;
	private Double longitude;	
	private Language language;
	private Float timezone;
	private String website;
	private String aboutMe;
	
	private Date dateCreated = new Date();
	private Date lastLoginDate = new Date();
	
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean enabled;	
	private boolean passwordExpired;
	private Boolean hideEmailId = true;	
	private Boolean sendNotification = true;
	private Boolean allowIdentifactionMail = true;
	private String icon;
	private String fbProfilePic;
	private Boolean sendDigest = true;
	
	private Integer version;

	private Set<Role> roles = new HashSet<Role>(0);
	@JsonIgnore
	private Set<Token> tokens;

	public User() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "suser_id_seq")
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id", nullable = false)
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "account_expired", nullable = false)
	public boolean isAccountExpired() {
		return this.accountExpired;
	}

	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	@Column(name = "account_locked", nullable = false)
	public boolean isAccountLocked() {
		return this.accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	@Column(name = "enabled", nullable = false)
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "password", nullable = false)
	@NotBlank
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "password_expired", nullable = false)
	public boolean isPasswordExpired() {
		return this.passwordExpired;
	}

	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	@Column(name = "username", nullable = false)
	@NotBlank
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "about_me", columnDefinition = "text")
	public String getAboutMe() {
		return this.aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", length = 29)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "email", unique = true, nullable = false)
	@NotBlank
	@Email
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "hide_email_id")
	public Boolean getHideEmailId() {
		return this.hideEmailId;
	}

	public void setHideEmailId(Boolean hideEmailId) {
		this.hideEmailId = hideEmailId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_date", length = 29)
	public Date getLastLoginDate() {
		return this.lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	@Column(name = "location")
	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name = "name")
	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "profile_pic")
	public String getProfilePic() {
		
		
		  //boolean iconPresent = this.getIcon()!= null;
			        if(this.getIcon() !=  null) {
			           // def thumbnailUrl =  grailsApplication.config.speciesPortal.users.serverURL + "/" + ImageUtils.getFileName(this.icon, type, null)
			            return this.getIcon();
			        }


			        else{
			            return profilePic;
			        }

	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	@Column(name = "send_notification")
	public Boolean getSendNotification() {
		return this.sendNotification;
	}

	public void setSendNotification(Boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	@Column(name = "timezone", precision = 8, scale = 8)
	public Float getTimezone() {
		return this.timezone;
	}

	public void setTimezone(Float timezone) {
		this.timezone = timezone;
	}

	@Column(name = "website")
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Column(name = "allow_identifaction_mail")
	public Boolean getAllowIdentifactionMail() {
		return this.allowIdentifactionMail;
	}

	public void setAllowIdentifactionMail(Boolean allowIdentifactionMail) {
		this.allowIdentifactionMail = allowIdentifactionMail;
	}

	@Column(name = "icon")
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Column(name = "fb_profile_pic")
	public String getFbProfilePic() {
		return this.fbProfilePic;
	}

	public void setFbProfilePic(String fbProfilePic) {
		this.fbProfilePic = fbProfilePic;
	}

	@Column(name = "send_digest")
	public Boolean getSendDigest() {
		return this.sendDigest;
	}

	public void setSendDigest(Boolean sendDigest) {
		this.sendDigest = sendDigest;
	}

	@Column(name = "institution_type")
	public String getInstitutionType() {
		return this.institutionType;
	}

	public void setInstitutionType(String institutionType) {
		this.institutionType = institutionType;
	}

	@Column(name = "occupation_type")
	public String getOccupationType() {
		return this.occupationType;
	}

	public void setOccupationType(String occupationType) {
		this.occupationType = occupationType;
	}

	@Column(name = "sex_type")
	public String getSexType() {
		return this.sexType;
	}

	public void setSexType(String sexType) {
		this.sexType = sexType;
	}

	@Column(name = "latitude", precision = 17, scale = 17)
	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitude", precision = 17, scale = 17)
	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "suser_role", schema = "public", joinColumns = {
			@JoinColumn(name = "s_user_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", nullable = false, updatable = false) })
	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public boolean hasRole(String role) {
		for (Role r : this.roles) {
			if (r.getAuthority().equalsIgnoreCase(role)) {
				return true;
			}
		}
		return false;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Token> getTokens() {
		return this.tokens;
	}

	public void setTokens(Set<Token> tokens) {
		this.tokens = tokens;
	}

	@Version
    @Column(name="version")
    public Integer getVersion() { 
		return version;
	}
	
	public void setVersion(int version) { 
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + "]";
	}
}
