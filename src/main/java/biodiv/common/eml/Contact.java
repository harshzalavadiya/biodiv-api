package biodiv.common.eml;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import biodiv.user.User;

@Entity
@Table(name = "contact", schema = "public")
public class Contact implements java.io.Serializable {

	private long id;
	private long version;
	private User user;
	private String address;
	private String city;
	private String country;
	private String deliveryPoint;
	private String description;
	private String firstName;
	private String lastName;
	private String name;
	private String organization;
	private String postalCode;
	private String role;
	private String state;
	private Set datasets = new HashSet(0);

	public Contact() {
	}

	public Contact(long id, User user, String address, String city, String country, String deliveryPoint,
			String description, String firstName, String lastName, String name, String organization, String postalCode,
			String role, String state) {
		this.id = id;
		this.user = user;
		this.address = address;
		this.city = city;
		this.country = country;
		this.deliveryPoint = deliveryPoint;
		this.description = description;
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.organization = organization;
		this.postalCode = postalCode;
		this.role = role;
		this.state = state;
	}

	public Contact(long id, User user, String address, String city, String country, String deliveryPoint,
			String description, String firstName, String lastName, String name, String organization, String postalCode,
			String role, String state, Set datasets) {
		this.id = id;
		this.user = user;
		this.address = address;
		this.city = city;
		this.country = country;
		this.deliveryPoint = deliveryPoint;
		this.description = description;
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.organization = organization;
		this.postalCode = postalCode;
		this.role = role;
		this.state = state;
		this.datasets = datasets;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version", nullable = false)
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "address", nullable = false)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", nullable = false)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "country", nullable = false)
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "delivery_point", nullable = false)
	public String getDeliveryPoint() {
		return this.deliveryPoint;
	}

	public void setDeliveryPoint(String deliveryPoint) {
		this.deliveryPoint = deliveryPoint;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "first_name", nullable = false)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "last_name", nullable = false)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "organization", nullable = false)
	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Column(name = "postal_code", nullable = false)
	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Column(name = "role", nullable = false)
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name = "state", nullable = false)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contact")
	public Set getDatasets() {
		return this.datasets;
	}

	public void setDatasets(Set datasets) {
		this.datasets = datasets;
	}

}
