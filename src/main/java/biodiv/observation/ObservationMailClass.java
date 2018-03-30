package biodiv.observation;

import java.util.Date;
import java.util.Set;

public class ObservationMailClass {
	
	private Long id;
	private String scientificName;
	private String commonName;
	private String location;
	private Date observedOn;
	private String icon;
	private Set userGroups;
	
	public Set getUserGroups() {
		return userGroups;
	}
	public void setUserGroups(Set userGroups) {
		this.userGroups = userGroups;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getScientificName() {
		return scientificName;
	}
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getObservedOn() {
		return observedOn;
	}
	public void setObservedOn(Date observedOn) {
		this.observedOn = observedOn;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

}
