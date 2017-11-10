package biodiv.taxon.datamodel.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import biodiv.user.User;

@Entity
@Table(name = "taxonomy_registry", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = { "path",
		"classification_id", "taxon_definition_id" }))
public class TaxonomyRegistry implements java.io.Serializable {

	private long id;
	private long version;
	private long classificationId;
	private long parent;
	private User suser;
	private TaxonomyRegistry taxonomyRegistry;
	private long taxonDefinitionId;
	private String path;
	private Date uploadTime;

	public TaxonomyRegistry() {

	}

	public TaxonomyRegistry(long id, String path) {
		this.id = id;
		this.path = path;
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

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "classification_id", nullable = false)
	// public Classification getClassification() {
	// return this.classification;
	// }

	// public void setClassification(Classification classification) {
	// this.classification = classification;
	// }

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "parent_taxon_definition_id")
	// public Taxon getTaxonomyDefinitionByParentTaxonDefinitionId() {
	// return this.taxonomyDefinitionByParentTaxonDefinitionId;
	// }
	//
	// public void setTaxonomyDefinitionByParentTaxonDefinitionId(
	// Taxon taxonomyDefinitionByParentTaxonDefinitionId) {
	// this.taxonomyDefinitionByParentTaxonDefinitionId =
	// taxonomyDefinitionByParentTaxonDefinitionId;
	// }

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uploader_id")
	public User getSuser() {
		return this.suser;
	}

	public void setSuser(User suser) {
		this.suser = suser;
	}

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "parent_taxon_id")
	// public TaxonomyRegistry getTaxonomyRegistry() {
	// return this.taxonomyRegistry;
	// }
	//
	// public void setTaxonomyRegistry(TaxonomyRegistry taxonomyRegistry) {
	// this.taxonomyRegistry = taxonomyRegistry;
	// }

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "taxon_definition_id", nullable = false)
	// public Taxon getTaxonomyDefinition() {
	// return this.taxonomyDefinition;
	// }
	//
	// public void setTaxonomyDefinition(Taxon taxonomyDefinition) {
	// this.taxonomyDefinition = taxonomyDefinition;
	// }

	@Column(name = "path", nullable = false)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "upload_time", length = 29)
	public Date getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	/*
	 * @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxonomyRegistry") public
	 * Set<User> getTaxonomyRegistryUsers() { return this.taxonomyRegistryUsers;
	 * }
	 * 
	 * public void setTaxonomyRegistryUsers(Set<User> taxonomyRegistrySusers) {
	 * this.taxonomyRegistryUsers = taxonomyRegistrySusers; }
	 */

	// @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxonomyRegistry")
	// public Set<TaxonomyRegistry> getTaxonomyRegistries() {
	// return this.taxonomyRegistries;
	// }
	//
	// public void setTaxonomyRegistries(Set<TaxonomyRegistry>
	// taxonomyRegistries) {
	// this.taxonomyRegistries = taxonomyRegistries;
	// }
	@Column(name = "taxon_definition_id", nullable = false, insertable = false, updatable = false)
	public long getTaxonDefinitionId() {
		return taxonDefinitionId;
	}

	public void setTaxonDefinitionId(long taxonDefinitionId) {
		this.taxonDefinitionId = taxonDefinitionId;
	}

	@Column(name = "classification_id", nullable = false)
	public long getClassificationId() {
		return classificationId;
	}

	public void setClassificationId(long classificationId) {
		classificationId = classificationId;
	}

	@Column(name = "parent_taxon_definition_id")
	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

}