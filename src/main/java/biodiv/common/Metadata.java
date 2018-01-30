package biodiv.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vividsolutions.jts.geom.Geometry;

import biodiv.dataset.Dataset;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Metadata implements GenericModel,java.io.Serializable {

	
	public enum LocationScale {
		APPROXIMATE ("Approximate"),
		ACCURATE ("Accurate"),
		LOCAL ("Local"),
		REGION ("Region"),
		COUNTRY ("Country");
		
		private String value;

		LocationScale(String value) {
			this.value = value;
		}

		String value() {
			return this.value;
		}
		
		static LocationScale getEnum(String value){
			if(value ==null || value.isEmpty()) return null;

			value = value.toUpperCase().trim();

			switch(value){
				case "APPROXIMATE":
					return LocationScale.APPROXIMATE;
				case "ACCURATE":
					return LocationScale.ACCURATE;
				case "LOCAL":
					return LocationScale.LOCAL;
				case "REGION":
					return LocationScale.REGION;
				case "COUNTRY":
					return LocationScale.COUNTRY;
				default:
					return null;
			}
		}
	}
	
	//Geographic Coverage
	private String placeName;
	private String reverseGeocodedName;
	
	private boolean geoPrivacy = false;
	//XXX to be removed after locationScale migration
	private String locationAccuracy;
	private LocationScale locationScale;
	
	private Geometry topology;
     
	private double latitude;
	private double longitude;
	
    //Taxonomic Coverage
	private SpeciesGroup group;
	private Habitat habitat;

    //Temporal Coverage
	private Date fromDate;
	private Date toDate;
	
	private Date createdOn = new Date();
	private Date lastRevised = createdOn;

    // Language
	private Language language;

	private License license;

	private String externalId;
	private String externalUrl;
	private String viaId;
	private String viaCode;

	private Date lastInterpreted;
	private Date lastCrawled;

	private Dataset dataset;

	public Metadata() {
		// TODO Auto-generated constructor stub
	}
	
	@Column(name = "place_name")
	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	@Column(name = "reverse_geocoded_name")
	public String getReverseGeocodedName() {
		return reverseGeocodedName;
	}

	public void setReverseGeocodedName(String reverseGeocodedName) {
		this.reverseGeocodedName = reverseGeocodedName;
	}

	@Column(name = "geo_privacy")
	public boolean isGeoPrivacy() {
		return geoPrivacy;
	}

	public void setGeoPrivacy(boolean geoPrivacy) {
		this.geoPrivacy = geoPrivacy;
	}

	@Column(name = "location_accuracy")
	public String getLocationAccuracy() {
		return locationAccuracy;
	}

	public void setLocationAccuracy(String locationAccuracy) {
		this.locationAccuracy = locationAccuracy;
	}

	@Column(name = "location_scale", nullable = false)
	@Enumerated(EnumType.STRING)
	public LocationScale getLocationScale() {
		return locationScale;
	}

	public void setLocationScale(LocationScale locationScale) {
		this.locationScale = locationScale;
	}

	@JsonIgnore
	@Column(name = "topology", nullable = false)
	public Geometry getTopology() {
		return topology;
	}

	public void setTopology(Geometry topology) {
		this.topology = topology;
	}

	@Column(name = "latitude", nullable = false, precision = 17, scale = 17)
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitude", nullable = false, precision = 17, scale = 17)
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "group_id", nullable = false)
	public SpeciesGroup getGroup() {
		return group;
	}

	public void setGroup(SpeciesGroup group) {
		this.group = group;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "habitat_id")
	public Habitat getHabitat() {
		return habitat;
	}

	public void setHabitat(Habitat habitat) {
		this.habitat = habitat;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "from_date", length = 29)
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "to_date", length = 29)
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false, length = 29)
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_revised", length = 29)
	public Date getLastRevised() {
		return lastRevised;
	}

	public void setLastRevised(Date lastRevised) {
		this.lastRevised = lastRevised;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "license_id", nullable = false)
	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@Column(name = "external_id")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column(name = "external_url")
	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	@Column(name = "via_id")
	public String getViaId() {
		return viaId;
	}

	public void setViaId(String viaId) {
		this.viaId = viaId;
	}

	@Column(name = "via_code")
	public String getViaCode() {
		return viaCode;
	}

	public void setViaCode(String viaCode) {
		this.viaCode = viaCode;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_interpreted", length = 29)
	public Date getLastInterpreted() {
		return lastInterpreted;
	}

	public void setLastInterpreted(Date lastInterpreted) {
		this.lastInterpreted = lastInterpreted;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_crawled", length = 29)
	public Date getLastCrawled() {
		return lastCrawled;
	}

	public void setLastCrawled(Date lastCrawled) {
		this.lastCrawled = lastCrawled;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dataset_id")
	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}


}
