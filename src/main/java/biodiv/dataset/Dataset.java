package biodiv.dataset;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import biodiv.common.Language;
import biodiv.common.License;
import biodiv.common.Ufile;
import biodiv.common.eml.Contact;
import biodiv.observation.Observation;
import biodiv.user.User;

@Entity
@Table(name = "dataset", schema = "public")
public class Dataset implements java.io.Serializable {

	private long id;
	private long version;
	private Language languageByLanguageId;
	private License license;
	private Datasource datasource;
	private User user;
	private Contact contact;
	private Ufile ufile;
	private Language languageByDataLanguageId;
	private String additionalInfo;
	private Date createdOn;
	private String description;
	private String externalId;
	private String externalUrl;
	private String geographicDescription;
	private boolean isDeleted;
	private Date lastRevised;
	private Date publicationDate;
	private String purpose;
	private String rights;
	private String title;
	private String type;
	private String viaCode;
	private String viaId;
	private String attribution;
	//private Set<Observation> observation = new HashSet<Observation>(0);

	public Dataset() {
	}

	public Dataset(long id, Language languageByLanguageId, License license, Datasource datasource, User user,
			Language languageByDataLanguageId, Date createdOn, String description, boolean isDeleted, Date lastRevised,
			Date publicationDate, String title, String type, String attribution) {
		this.id = id;
		this.languageByLanguageId = languageByLanguageId;
		this.license = license;
		this.datasource = datasource;
		this.user = user;
		this.languageByDataLanguageId = languageByDataLanguageId;
		this.createdOn = createdOn;
		this.description = description;
		this.isDeleted = isDeleted;
		this.lastRevised = lastRevised;
		this.publicationDate = publicationDate;
		this.title = title;
		this.type = type;
		this.attribution = attribution;
	}

	public Dataset(long id, Language languageByLanguageId, License license, Datasource datasource, User user,
			Contact contact, Ufile ufile, Language languageByDataLanguageId, String additionalInfo, Date createdOn,
			String description, String externalId, String externalUrl, String geographicDescription, boolean isDeleted,
			Date lastRevised, Date publicationDate, String purpose, String rights, String title, String type,
			String viaCode, String viaId, String attribution) {
		this.id = id;
		this.languageByLanguageId = languageByLanguageId;
		this.license = license;
		this.datasource = datasource;
		this.user = user;
		this.contact = contact;
		this.ufile = ufile;
		this.languageByDataLanguageId = languageByDataLanguageId;
		this.additionalInfo = additionalInfo;
		this.createdOn = createdOn;
		this.description = description;
		this.externalId = externalId;
		this.externalUrl = externalUrl;
		this.geographicDescription = geographicDescription;
		this.isDeleted = isDeleted;
		this.lastRevised = lastRevised;
		this.publicationDate = publicationDate;
		this.purpose = purpose;
		this.rights = rights;
		this.title = title;
		this.type = type;
		this.viaCode = viaCode;
		this.viaId = viaId;
		this.attribution = attribution;
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
	@JoinColumn(name = "language_id", nullable = false)
	public Language getLanguageByLanguageId() {
		return this.languageByLanguageId;
	}

	public void setLanguageByLanguageId(Language languageByLanguageId) {
		this.languageByLanguageId = languageByLanguageId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "license_id", nullable = false)
	public License getLicense() {
		return this.license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "datasource_id", nullable = false)
	public Datasource getDatasource() {
		return this.datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "original_author_id")
	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "u_file_id")
	public Ufile getUfile() {
		return this.ufile;
	}

	public void setUfile(Ufile ufile) {
		this.ufile = ufile;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_language_id", nullable = false)
	public Language getLanguageByDataLanguageId() {
		return this.languageByDataLanguageId;
	}

	public void setLanguageByDataLanguageId(Language languageByDataLanguageId) {
		this.languageByDataLanguageId = languageByDataLanguageId;
	}

	@Column(name = "additional_info")
	public String getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false, length = 29)
	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "external_id")
	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column(name = "external_url")
	public String getExternalUrl() {
		return this.externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	@Column(name = "geographic_description")
	public String getGeographicDescription() {
		return this.geographicDescription;
	}

	public void setGeographicDescription(String geographicDescription) {
		this.geographicDescription = geographicDescription;
	}

	@Column(name = "is_deleted", nullable = false)
	public boolean isIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_revised", nullable = false, length = 29)
	public Date getLastRevised() {
		return this.lastRevised;
	}

	public void setLastRevised(Date lastRevised) {
		this.lastRevised = lastRevised;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "publication_date", nullable = false, length = 29)
	public Date getPublicationDate() {
		return this.publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	@Column(name = "purpose")
	public String getPurpose() {
		return this.purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@Column(name = "rights")
	public String getRights() {
		return this.rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "type", nullable = false)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "via_code")
	public String getViaCode() {
		return this.viaCode;
	}

	public void setViaCode(String viaCode) {
		this.viaCode = viaCode;
	}

	@Column(name = "via_id")
	public String getViaId() {
		return this.viaId;
	}

	public void setViaId(String viaId) {
		this.viaId = viaId;
	}

	@Column(name = "attribution", nullable = false)
	public String getAttribution() {
		return this.attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

/*	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataset")
	public Set<Observation> getObservation() {
		return this.observation;
	}

	public void setObservation(Set<Observation> observation) {
		this.observation = observation;
	}
*/
}
