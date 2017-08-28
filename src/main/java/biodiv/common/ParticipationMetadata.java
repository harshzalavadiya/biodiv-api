package biodiv.common;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.validator.constraints.NotBlank;

import biodiv.user.User;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class ParticipationMetadata extends Metadata {
	
	User author;// or uploader from sourcedata
	String originalAuthor;

	int rating;
	long visitCount = 0;
	int flagCount = 0;
	int featureCount = 0;
	boolean isDeleted = false;

/*TODO:    static mapping = {
        tablePerHierarchy false
    }
*/
    @Column
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Column
	public String getOriginalAuthor() {
		return originalAuthor;
	}

	public void setOriginalAuthor(String originalAuthor) {
		this.originalAuthor = originalAuthor;
	}

	@Column
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Column
	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

	@Column
	public int getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(int flagCount) {
		this.flagCount = flagCount;
	}

	@Column(nullable = false)
	public int getFeatureCount() {
		return featureCount;
	}

	public void setFeatureCount(int featureCount) {
		this.featureCount = featureCount;
	}

	@Column
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
