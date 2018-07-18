package biodiv.observation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.user.User;

@Entity
@Table(name = "recommendation", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"taxon_concept_id", "accepted_name_id", "name", "language_id" }))
@Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Recommendation implements java.io.Serializable {

	private long id;
	//private TaxonomyDefinition taxonomyDefinitionByAcceptedNameId;
	//private TaxonomyDefinition taxonomyDefinitionByTaxonConceptId;
	private Date lastModified;
	private String name;
	private Boolean isScientificName;
	private Long languageId;
	private String lowercaseName;
	private String flaggingReason;
	private Boolean isFlagged;
	private Taxon taxonConcept;
	private Taxon acceptedName;
	

	public Recommendation() {
	}

	public Recommendation( Date lastModified, String name) {
		//this.id = id;
		this.lastModified = lastModified;
		this.name = name;
	}

	public Recommendation(Date lastModified, String name,
			Boolean isScientificName, Long languageId, String lowercaseName, String flaggingReason, Boolean isFlagged) {
		//this.id = id;
		this.lastModified = lastModified;
		this.name = name;
		this.isScientificName = isScientificName;
		this.languageId = languageId;
		this.lowercaseName = lowercaseName;
		this.flaggingReason = flaggingReason;
		this.isFlagged = isFlagged;
	}
	
	public Recommendation(Date lastModified,String name,String lowercaseName,Taxon taxonConcept,Boolean isScientificName,Long languageId){
		
		this.lastModified = lastModified;
		this.name = name;
		this.lowercaseName = lowercaseName;
		this.taxonConcept = taxonConcept;
		this.isScientificName = isScientificName;
		this.languageId = languageId;
		this.isFlagged = false;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accepted_name_id")
	public Taxon getAcceptedName() {
		return this.acceptedName;
	}
	
	public void setAcceptedName(Taxon acceptedName) {
		this.acceptedName = acceptedName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taxon_concept_id")
	public Taxon getTaxonConcept() {
		return this.taxonConcept;
	}

	public void setTaxonConcept(Taxon taxonConcept) {
		this.taxonConcept = taxonConcept;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_modified", nullable = false, length = 29)
	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "is_scientific_name")
	public Boolean getIsScientificName() {
		return this.isScientificName;
	}

	public void setIsScientificName(Boolean isScientificName) {
		this.isScientificName = isScientificName;
	}

	@Column(name = "language_id")
	public Long getLanguageId() {
		return this.languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	@Column(name = "lowercase_name")
	public String getLowercaseName() {
		return this.lowercaseName;
	}

	public void setLowercaseName(String lowercaseName) {
		this.lowercaseName = lowercaseName;
	}

	@Column(name = "flagging_reason", length = 1500)
	public String getFlaggingReason() {
		return this.flaggingReason;
	}

	public void setFlaggingReason(String flaggingReason) {
		this.flaggingReason = flaggingReason;
	}

	@Column(name = "is_flagged")
	public Boolean getIsFlagged() {
		return this.isFlagged;
	}

	public void setIsFlagged(Boolean isFlagged) {
		this.isFlagged = isFlagged;
	}

	
}