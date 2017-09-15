package biodiv.common;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.NotBlank;

import biodiv.user.User;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class ParticipationMetadata extends Metadata {
	
	User author;// or uploader from sourcedata
	String originalAuthor;

	int rating;
	long visitCount = 0;
	int flagCount = 0;
	int featureCount = 0;
	boolean isDeleted = false;

	public ParticipationMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Column(name = "original_author")
	public String getOriginalAuthor() {
		return originalAuthor;
	}

	public void setOriginalAuthor(String originalAuthor) {
		this.originalAuthor = originalAuthor;
	}

	@Column(name = "rating", nullable = false)
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Column(name = "visit_count")
	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

	@Column(name = "flag_count")
	public int getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(int flagCount) {
		this.flagCount = flagCount;
	}

	@Column(name = "feature_count")
	public int getFeatureCount() {
		return featureCount;
	}

	public void setFeatureCount(int featureCount) {
		this.featureCount = featureCount;
	}

	@Column(name = "is_deleted")
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

    public void incrementPageVisit(){
        this.visitCount++;
    }    
}
