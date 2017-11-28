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
import biodiv.user.User;

@Entity
@Table(name = "datasource", schema = "public")
public class Datasource implements java.io.Serializable {

	private long id;
	private long version;
	private Language language;
	private User  user;
	private Date createdOn;
	private String description;
	private String icon;
	private boolean isDeleted;
	private Date lastRevised;
	private String title;
	private String website;
	private Set datasets = new HashSet(0);

	public Datasource() {
	}

	public Datasource(long id, Language language, User user, Date createdOn, String description, boolean isDeleted,
			Date lastRevised, String title, String website) {
		this.id = id;
		this.language = language;
		this.user = user;
		this.createdOn = createdOn;
		this.description = description;
		this.isDeleted = isDeleted;
		this.lastRevised = lastRevised;
		this.title = title;
		this.website = website;
	}

	public Datasource(long id, Language language, User user, Date createdOn, String description, String icon,
			boolean isDeleted, Date lastRevised, String title, String website, Set datasets) {
		this.id = id;
		this.language = language;
		this.user = user;
		this.createdOn = createdOn;
		this.description = description;
		this.icon = icon;
		this.isDeleted = isDeleted;
		this.lastRevised = lastRevised;
		this.title = title;
		this.website = website;
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
	@JoinColumn(name = "language_id", nullable = false)
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
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

	@Column(name = "icon")
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "website", nullable = false)
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "datasource")
	public Set<Dataset> getDatasets() {
		return this.datasets;
	}

	public void setDatasets(Set<Dataset> datasets) {
		this.datasets = datasets;
	}

}
