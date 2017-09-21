package biodiv.taxon;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import biodiv.common.ExternalLinks;
import biodiv.common.SpeciesGroup;
import biodiv.user.User;


@Entity
@Table(name = "taxonomy_definition", schema = "public")
public class TaxonomyDefinition implements java.io.Serializable {

	private long id;
	private SpeciesGroup speciesGroup;
	private User user;
	private ExternalLinks externalLinks;
	private String binomialForm;
	private String canonicalForm;
	private String italicisedForm;
	private String name;
	private String normalizedForm;
	private int rank;
	private String threatenedStatus;
	private Date uploadTime;
	private String status;
	private String position;
	private String authorYear;
	private String matchDatabaseName;
	private String matchId;
	private String ibpSource;
	private String viaDatasource;
	private Boolean isFlagged;
	private String lowercaseMatchName;
	private String colNameStatus;
	private String oldId;
	private String relationship;
	//private String class_;
	private String flaggingReason;
	private Integer noOfcolmatches;
	private Boolean isDeleted;
	private String dirtyListReason;
	private String activityDescription;
	private String defaultHierarchy;
	private String nameSourceId;
	@JsonIgnore
	private String traits;
	@JsonIgnore
	private String traitsJson;
	/*private Set commonNameses = new HashSet(0);
	private Set namePermissionsForNodeId = new HashSet(0);
	private Set taxonomyRegistriesForParentTaxonDefinitionId = new HashSet(0);
	private Set recommendationsForTaxonConceptId = new HashSet(0);
	private Set acceptedSynonymsForSynonymId = new HashSet(0);
	private Set namePermissionsForRootNodeId = new HashSet(0);
	private Set speciesGroupMappings = new HashSet(0);
	private Set speciesPermissions = new HashSet(0);
	private Set taxonomyDefinitionYears = new HashSet(0);
	private Set docSciNames = new HashSet(0);
	private Set taxonomyRegistriesForTaxonDefinitionId = new HashSet(0);
	private Set acceptedSynonymsForAcceptedId = new HashSet(0);
	private Set synonymses = new HashSet(0);
	private Set traitTaxonomyDefinitions = new HashSet(0);
	private Set facts = new HashSet(0);
	private Set recommendationsForAcceptedNameId = new HashSet(0);
	private Set taxonomyDefinitionAuthors = new HashSet(0);
	private Set taxonomyDefinitionUsers = new HashSet(0);
	private Set specieses = new HashSet(0);
*/
	public TaxonomyDefinition() {
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public SpeciesGroup getSpeciesGroup() {
		return this.speciesGroup;
	}

	public void setSpeciesGroup(SpeciesGroup speciesGroup) {
		this.speciesGroup = speciesGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uploader_id")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "external_links_id")
	public ExternalLinks getExternalLinks() {
		return this.externalLinks;
	}

	public void setExternalLinks(ExternalLinks externalLinks) {
		this.externalLinks = externalLinks;
	}

	@Column(name = "binomial_form")
	public String getBinomialForm() {
		return this.binomialForm;
	}

	public void setBinomialForm(String binomialForm) {
		this.binomialForm = binomialForm;
	}

	@Column(name = "canonical_form", nullable = false)
	public String getCanonicalForm() {
		return this.canonicalForm;
	}

	public void setCanonicalForm(String canonicalForm) {
		this.canonicalForm = canonicalForm;
	}

	@Column(name = "italicised_form")
	public String getItalicisedForm() {
		return this.italicisedForm;
	}

	public void setItalicisedForm(String italicisedForm) {
		this.italicisedForm = italicisedForm;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "normalized_form")
	public String getNormalizedForm() {
		return this.normalizedForm;
	}

	public void setNormalizedForm(String normalizedForm) {
		this.normalizedForm = normalizedForm;
	}

	@Column(name = "rank", nullable = false)
	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Column(name = "threatened_status")
	public String getThreatenedStatus() {
		return this.threatenedStatus;
	}

	public void setThreatenedStatus(String threatenedStatus) {
		this.threatenedStatus = threatenedStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "upload_time", length = 29)
	public Date getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Column(name = "status")
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "position")
	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "author_year")
	public String getAuthorYear() {
		return this.authorYear;
	}

	public void setAuthorYear(String authorYear) {
		this.authorYear = authorYear;
	}

	@Column(name = "match_database_name")
	public String getMatchDatabaseName() {
		return this.matchDatabaseName;
	}

	public void setMatchDatabaseName(String matchDatabaseName) {
		this.matchDatabaseName = matchDatabaseName;
	}

	@Column(name = "match_id")
	public String getMatchId() {
		return this.matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	@Column(name = "ibp_source")
	public String getIbpSource() {
		return this.ibpSource;
	}

	public void setIbpSource(String ibpSource) {
		this.ibpSource = ibpSource;
	}

	@Column(name = "via_datasource")
	public String getViaDatasource() {
		return this.viaDatasource;
	}

	public void setViaDatasource(String viaDatasource) {
		this.viaDatasource = viaDatasource;
	}

	@Column(name = "is_flagged")
	public Boolean getIsFlagged() {
		return this.isFlagged;
	}

	public void setIsFlagged(Boolean isFlagged) {
		this.isFlagged = isFlagged;
	}

	@Column(name = "lowercase_match_name")
	public String getLowercaseMatchName() {
		return this.lowercaseMatchName;
	}

	public void setLowercaseMatchName(String lowercaseMatchName) {
		this.lowercaseMatchName = lowercaseMatchName;
	}

	@Column(name = "col_name_status")
	public String getColNameStatus() {
		return this.colNameStatus;
	}

	public void setColNameStatus(String colNameStatus) {
		this.colNameStatus = colNameStatus;
	}

	@Column(name = "old_id")
	public String getOldId() {
		return this.oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	@Column(name = "relationship")
	public String getRelationship() {
		return this.relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	@Column(name = "flagging_reason", length = 1500)
	public String getFlaggingReason() {
		return this.flaggingReason;
	}

	public void setFlaggingReason(String flaggingReason) {
		this.flaggingReason = flaggingReason;
	}

	@Column(name = "no_ofcolmatches")
	public Integer getNoOfcolmatches() {
		return this.noOfcolmatches;
	}

	public void setNoOfcolmatches(Integer noOfcolmatches) {
		this.noOfcolmatches = noOfcolmatches;
	}

	@Column(name = "is_deleted")
	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Column(name = "dirty_list_reason", length = 1000)
	public String getDirtyListReason() {
		return this.dirtyListReason;
	}

	public void setDirtyListReason(String dirtyListReason) {
		this.dirtyListReason = dirtyListReason;
	}

	@Column(name = "activity_description", length = 2000)
	public String getActivityDescription() {
		return this.activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	@Column(name = "default_hierarchy")
	public String getDefaultHierarchy() {
		return this.defaultHierarchy;
	}

	public void setDefaultHierarchy(String defaultHierarchy) {
		this.defaultHierarchy = defaultHierarchy;
	}

	@Column(name = "name_source_id")
	public String getNameSourceId() {
		return this.nameSourceId;
	}

	public void setNameSourceId(String nameSourceId) {
		this.nameSourceId = nameSourceId;
	}

	@Column(name = "traits")
	public String getTraits() {
		return this.traits;
	}

	public void setTraits(String traits) {
		this.traits = traits;
	}

	@Column(name = "traits_json")
	public String getTraitsJson() {
		return this.traitsJson;
	}

	public void setTraitsJson(String traitsJson) {
		this.traitsJson = traitsJson;
	}

}
