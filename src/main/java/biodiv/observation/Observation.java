package biodiv.observation;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import biodiv.common.DataObject;
import biodiv.resource.Resource;

@Entity
@Table(name = "observation", uniqueConstraints = { @UniqueConstraint(columnNames = "id"), })
@NamedQuery(name = Observation.QUERY_SELECT_BY_ID, query = "SELECT obv FROM Observation obv WHERE obv.id = :"
		+ Observation.PARAM_ID)
public class Observation extends DataObject {

	public static final String QUERY_SELECT_BY_ID = "Observation.findById";
	public static final String PARAM_ID = "id";

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

	String notes;
	// boolean isDeleted = false;
	String searchText;
	// if observation locked due to pulling of images in species
	boolean isLocked = false;
	Recommendation maxVotedReco;
	boolean agreeTerms = false;

	// if true observation comes on view otherwise not
	boolean isShowable;
	// if observation representing checklist then this flag is true
	boolean isChecklist = false;
	// observation generated from checklist will have source as
	// checklist other will point to themself
	Long sourceId;

	// column to store checklist key value pair in serialized object
	String checklistAnnotations;
	BasisOfRecord basisOfRecord = BasisOfRecord.HUMAN_OBSERVATION;
	ProtocolType protocol = ProtocolType.SINGLE_OBSERVATION;
	String externalDatasetKey;
	Date lastCrawled;
	String catalogNumber;
	String publishingCountry = "IN";
	String accessRights;
	String informationWithheld;

	Resource reprImage;

	int noOfImages = 0;
	int noOfVideos = 0;
	int noOfAudio = 0;
	int noOfIdentifications = 0;

//	static hasMany=[userGroups:UserGroup,resource:Resource,recommendationVote:RecommendationVote,annotations:Annotation];static belongsTo=[SUser,UserGroup,Checklists,Dataset]

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observation_generator")
	@SequenceGenerator(name = "observation_generator", sequenceName = "observation_id_seq")
	@Column(name = "id", unique = true, updatable = false, nullable = false)
	private Long id;

	public Observation() {
		// this form is used by hibernate
	}

	public Observation(Map properties) {
		// map properties of observation using reflection to the keys of the map
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return "Observation [id=" + id + "]";
	}

}
