package biodiv.observation;

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
@Table(name = "recommendation_vote", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"author_id", "observation_id" }))
public class RecommendationVote implements java.io.Serializable {

	private long id;
	private long version;
	private Recommendation recommendationByRecommendationId;
	private User user;
	private Observation observation;
	private Recommendation recommendationByCommonNameRecoId;
	private String confidence;
	private float userWeight;
	private Date votedOn;
	private String comment;
	private String givenCommonName;
	private String givenSciName;
	private String originalAuthor;

	public RecommendationVote() {
	}

	public RecommendationVote(long id, Recommendation recommendationByRecommendationId, User user,
			Observation observation, float userWeight, Date votedOn) {
		this.id = id;
		this.recommendationByRecommendationId = recommendationByRecommendationId;
		this.user = user;
		this.observation = observation;
		this.userWeight = userWeight;
		this.votedOn = votedOn;
	}

	public RecommendationVote(long id, Recommendation recommendationByRecommendationId, User user,
			Observation observation, Recommendation recommendationByCommonNameRecoId, String confidence,
			float userWeight, Date votedOn, String comment, String givenCommonName, String givenSciName,
			String originalAuthor) {
		this.id = id;
		this.recommendationByRecommendationId = recommendationByRecommendationId;
		this.user = user;
		this.observation = observation;
		this.recommendationByCommonNameRecoId = recommendationByCommonNameRecoId;
		this.confidence = confidence;
		this.userWeight = userWeight;
		this.votedOn = votedOn;
		this.comment = comment;
		this.givenCommonName = givenCommonName;
		this.givenSciName = givenSciName;
		this.originalAuthor = originalAuthor;
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
	@JoinColumn(name = "recommendation_id", nullable = false)
	public Recommendation getRecommendationByRecommendationId() {
		return this.recommendationByRecommendationId;
	}

	public void setRecommendationByRecommendationId(Recommendation recommendationByRecommendationId) {
		this.recommendationByRecommendationId = recommendationByRecommendationId;
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
	@JoinColumn(name = "observation_id", nullable = false)
	public Observation getObservation() {
		return this.observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "common_name_reco_id")
	public Recommendation getRecommendationByCommonNameRecoId() {
		return this.recommendationByCommonNameRecoId;
	}

	public void setRecommendationByCommonNameRecoId(Recommendation recommendationByCommonNameRecoId) {
		this.recommendationByCommonNameRecoId = recommendationByCommonNameRecoId;
	}

	@Column(name = "confidence")
	public String getConfidence() {
		return this.confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	@Column(name = "user_weight", nullable = false, precision = 8, scale = 8)
	public float getUserWeight() {
		return this.userWeight;
	}

	public void setUserWeight(float userWeight) {
		this.userWeight = userWeight;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "voted_on", nullable = false, length = 29)
	public Date getVotedOn() {
		return this.votedOn;
	}

	public void setVotedOn(Date votedOn) {
		this.votedOn = votedOn;
	}

	@Column(name = "comment")
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "given_common_name")
	public String getGivenCommonName() {
		return this.givenCommonName;
	}

	public void setGivenCommonName(String givenCommonName) {
		this.givenCommonName = givenCommonName;
	}

	@Column(name = "given_sci_name")
	public String getGivenSciName() {
		return this.givenSciName;
	}

	public void setGivenSciName(String givenSciName) {
		this.givenSciName = givenSciName;
	}

	@Column(name = "original_author")
	public String getOriginalAuthor() {
		return this.originalAuthor;
	}

	public void setOriginalAuthor(String originalAuthor) {
		this.originalAuthor = originalAuthor;
	}

}
