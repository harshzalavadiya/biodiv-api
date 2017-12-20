package biodiv.observation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;



import com.fasterxml.jackson.annotation.JsonValue;
import com.vividsolutions.jts.geom.Geometry;

import biodiv.common.DataObject;
import biodiv.common.GenericModel;
import biodiv.resource.Resource;
import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupModel;

@Entity
@Table(name = "observation", uniqueConstraints = { @UniqueConstraint(columnNames = "id"), })
@NamedQuery(name = Observation.QUERY_SELECT_BY_ID, query = "SELECT obv FROM Observation obv WHERE obv.id = :"
		+ Observation.PARAM_ID)


public class Observation extends DataObject {
	
//	@Inject
//	ObservationService observationService;
	public static final String QUERY_SELECT_BY_ID = "Observation.findById";
	public static final String PARAM_ID = "id";
	
//	@Inject
//	ObservationService observationService;

	public enum OccurrenceStatus {
		ABSENT("Absent"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#absent
		CASUAL("Casual"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#casual
		COMMON("Common"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#common
		DOUBTFUL("Doubtful"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#doubtful
		FAIRLYCOMMON("FairlyCommon"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#fairlyCommon
		IRREGULAR("Irregular"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#irregular
		PRESENT("Present"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#present
		RARE("Rare"), // http://rs.gbif.org/terms/1.0/occurrenceStatus#rare
		UNCOMMON("Uncommon");

		private String value;

		OccurrenceStatus(String value) {
			this.value = value;
		}
		
		@JsonValue
		String value() {
			return this.value;
		}
	}

	public enum BasisOfRecord {
		PRESERVED_SPECIMEN("Preserved Specimen"), 
		FOSSIL_SPECIMEN("Fossil Specimen"), 
		LIVING_SPECIMEN("Living Specimen"), 
		HUMAN_OBSERVATION("Human Observation"), 
		MACHINE_OBSERVATION("Machine Observation"), 
		MATERIAL_SAMPLE("Material Sample"), 
		OBSERVATION("Observation"), 
		UNKNOWN("Unknown");

		private String value;

		BasisOfRecord(String value) {
			this.value = value;
		}
		
		@JsonValue
		String value() {
			return this.value;
		}

		static BasisOfRecord getEnum(String value) {
			if (value == null || value.isEmpty())
				return null;

			value = value.toUpperCase().trim();
			switch (value) {
			case "PRESERVED_SPECIMEN":
				return BasisOfRecord.PRESERVED_SPECIMEN;
			case "FOSSIL_SPECIMEN":
				return BasisOfRecord.FOSSIL_SPECIMEN;
			case "LIVING_SPECIMEN":
				return BasisOfRecord.LIVING_SPECIMEN;
			case "HUMAN_OBSERVATION":
				return BasisOfRecord.HUMAN_OBSERVATION;
			case "MACHINE_OBSERVATION":
				return BasisOfRecord.MACHINE_OBSERVATION;
			case "MATERIAL_SAMPLE":
				return BasisOfRecord.MATERIAL_SAMPLE;
			case "OBSERVATION":
				return BasisOfRecord.OBSERVATION;

			default:
				return BasisOfRecord.UNKNOWN;
			}
		}
	}

	public enum ProtocolType {
		// https://github.com/gbif/gbif-api/blob/master/src/main/java/org/gbif/api/vocabulary/EndpointType.java
		DWC_ARCHIVE, // observations as dwc archive
		TEXT, // CSV upload ... for datasets mainly
		LIST, // Checklist
		SINGLE_OBSERVATION, // Single observation through UI
		MULTI_OBSERVATION, // Multiple observations thru UI
		BULK_UPLOAD, // XLS File upload of observations along with images
		MOBILE, // obvs uploaded thru mobile interface
		OTHER;

		private String value;

		@JsonValue
		String value() {
			return this.value;
		}

		static ProtocolType getEnum(String value) {
			if (value == null || value.isEmpty())
				return null;
			value = value.toUpperCase().trim();
			switch (value) {
			case "DWC_ARCHIVE":
				return ProtocolType.DWC_ARCHIVE;
			case "TEXT":
				return ProtocolType.TEXT;
			case "LIST":
				return ProtocolType.LIST;
			case "SINGLE_OBSERVATION":
				return ProtocolType.SINGLE_OBSERVATION;
			case "MULTI_OBSERVATION":
				return ProtocolType.MULTI_OBSERVATION;
			case "MOBILE":
				return ProtocolType.MOBILE;
			case "OTHER":
				return ProtocolType.OTHER;
			default:
				return null;
			}
		}
	}

	private String notes;
	// boolean isDeleted = false;
	private String searchText;
	// if observation locked due to pulling of images in species
	private boolean isLocked = false;
	private Recommendation maxVotedReco;
	private boolean agreeTerms = false;

	// if true observation comes on view otherwise not
	private boolean isShowable;
	// if observation representing checklist then this flag is true
	private boolean isChecklist = false;
	// observation generated from checklist will have source as
	// checklist other will point to themself
	private Long sourceId;

	// column to store checklist key value pair in serialized object
	private String checklistAnnotations;
	private BasisOfRecord basisOfRecord = BasisOfRecord.HUMAN_OBSERVATION;
	private ProtocolType protocol = ProtocolType.SINGLE_OBSERVATION;
	private String externalDatasetKey;
	private Date lastCrawled;
	private String catalogNumber;
	private String publishingCountry = "IN";
	private String accessRights;
	private String informationWithheld;

	private Resource reprImage;

	private int noOfImages = 0;
	private int noOfVideos = 0;
	private int noOfAudio = 0;
	private int noOfIdentifications = 0;

	private Set resources = new HashSet(0);
	private Set recommendationVotes = new HashSet(0);
	
//	@SuppressWarnings("unchecked")
//	@Access(value = AccessType.FIELD)
//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "user_group_observations", schema = "public", joinColumns = {
//			@JoinColumn(name = "observation_id", nullable = false, updatable = false) }, inverseJoinColumns = {
//					@JoinColumn(name = "user_group_id", nullable = false, updatable = false) })
	private Set userGroups = new HashSet(0);
	private Set annotations = new HashSet(0);
	
//	static hasMany=[userGroups:UserGroup,resource:Resource,recommendationVote:RecommendationVote,annotations:Annotation];
//	static belongsTo=[SUser,UserGroup,Checklists,Dataset]

	private Long id;
	
	public String lis(){
		System.out.println("hhhhhhhhhh");
		String x = "abc";
		return x;
	}

	public Observation() {
		// this form is used by hibernate
		//super(Observation.class);
	}

	public Observation(Map properties) {
		// map properties of observation using reflection to the keys of the map
		//super(Observation.class);
	}
	

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observation_generator")
	@SequenceGenerator(name = "observation_generator", sequenceName = "observation_id_seq")
	@Column(name = "id", unique = true, updatable = false, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "notes", columnDefinition="text")
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "search_text", columnDefinition="text")
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	@Column(name = "is_locked")
	public boolean isLocked() {
		return isLocked;
	}
	
	
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "max_voted_reco_id")
	public Recommendation getMaxVotedReco() {
		return maxVotedReco;
	}

	public void setMaxVotedReco(Recommendation maxVotedReco) {
		this.maxVotedReco = maxVotedReco;
	}

	@Column(name = "agree_terms")
	public boolean isAgreeTerms() {
		return agreeTerms;
	}

	public void setAgreeTerms(boolean agreeTerms) {
		this.agreeTerms = agreeTerms;
	}

	@Column(name = "is_showable")
	public boolean isShowable() {
		return isShowable;
	}

	public void setShowable(boolean isShowable) {
		this.isShowable = isShowable;
	}
	
	@Column(name = "is_checklist")
	public boolean isChecklist() {
		return isChecklist;
	}

	public void setChecklist(boolean isChecklist) {
		this.isChecklist = isChecklist;
	}

	@Column(name = "source_id")
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	@Column(name = "checklist_annotations", columnDefinition="text")
	public String getChecklistAnnotations() {
		return checklistAnnotations;
	}

	public void setChecklistAnnotations(String checklistAnnotations) {
		this.checklistAnnotations = checklistAnnotations;
	}

	@Column(name = "basis_of_record", nullable = false)
	@Enumerated(EnumType.STRING)
	public BasisOfRecord getBasisOfRecord() {
		return basisOfRecord;
	}

	public void setBasisOfRecord(BasisOfRecord basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}

	@Column(name = "protocol", nullable = false)
	@Enumerated(EnumType.STRING)
	public ProtocolType getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolType protocol) {
		this.protocol = protocol;
	}

	@Column(name = "external_dataset_key")
	public String getExternalDatasetKey() {
		return externalDatasetKey;
	}

	public void setExternalDatasetKey(String externalDatasetKey) {
		this.externalDatasetKey = externalDatasetKey;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_crawled")
	public Date getLastCrawled() {
		return lastCrawled;
	}

	public void setLastCrawled(Date lastCrawled) {
		this.lastCrawled = lastCrawled;
	}

	@Column(name = "catalog_number")
	public String getCatalogNumber() {
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	@Column(name = "publishing_country")
	public String getPublishingCountry() {
		return publishingCountry;
	}

	public void setPublishingCountry(String publishingCountry) {
		this.publishingCountry = publishingCountry;
	}

	
	@Column(name = "access_rights")
	public String getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}

	@Column(name = "information_withheld")
	public String getInformationWithheld() {
		return informationWithheld;
	}

	public void setInformationWithheld(String informationWithheld) {
		this.informationWithheld = informationWithheld;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repr_image_id")
	public Resource getReprImage() {
		return reprImage;
	}

	public void setReprImage(Resource reprImage) {
		this.reprImage = reprImage;
	}

	@Column(name = "no_of_images", nullable = false)
	public int getNoOfImages() {
		return noOfImages;
	}

	public void setNoOfImages(int noOfImages) {
		this.noOfImages = noOfImages;
	}

	@Column(name = "no_of_videos", nullable = false)
	public int getNoOfVideos() {
		return noOfVideos;
	}

	public void setNoOfVideos(int noOfVideos) {
		this.noOfVideos = noOfVideos;
	}

	@Column(name = "no_of_audio", nullable = false)
	public int getNoOfAudio() {
		return noOfAudio;
	}

	public void setNoOfAudio(int noOfAudio) {
		this.noOfAudio = noOfAudio;
	}

	@Column(name = "no_of_identifications", nullable = false)
	public int getNoOfIdentifications() {
		return noOfIdentifications;
	}

	public void setNoOfIdentifications(int noOfIdentifications) {
		this.noOfIdentifications = noOfIdentifications;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "observation_resource", schema = "public", joinColumns = {
			@JoinColumn(name = "observation_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "resource_id", nullable = false, updatable = false) })
	public Set<Resource> getResources() {
		return null;//this.resources;x
	}
	
	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "observation")
	public Set<RecommendationVote> getRecommendationVotes() {
		return null;//this.recommendationVotes;
	}
	
	public void setRecommendationVotes(Set<RecommendationVote> recommendationVotes) {
		this.recommendationVotes = recommendationVotes;
	}

	@SuppressWarnings("unchecked")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_group_observations", schema = "public", joinColumns = {
			@JoinColumn(name = "observation_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "user_group_id", nullable = false, updatable = false) })
	public Set<UserGroup> getUserGroups() {
		System.out.println("aaaaye");
		return this.userGroups;
	}
	
	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "observation")
	public Set<Annotation> getAnnotations() {
		return null;//this.annotations;
	}
	
	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
	
	public Observation get(long obvId,ObservationService observationService){
		System.out.println("testing injection in observation "+ observationService);
		Observation obv = observationService.findById(obvId);
		return obv;
	}
	
	public static boolean obvIsWithinUserGroupBoundary(Geometry topology, Geometry boundary) {
		boolean belongs = false;
		try{
			belongs = boundary.covers(topology);
			System.out.println("belongs or not " + belongs);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
		return belongs;
	}

	
	public String toString() {
		return "Observation [id=" + id + "]";
	}

//	@Override
//	public Observation get(long obvId) {
//		// TODO Auto-generated method stub
//		System.out.println("pfday");
//		ObservationService observationService =new ObservationService();
//		Observation result=observationService.findById(obvId);
//		return result;
//	}
	

}
