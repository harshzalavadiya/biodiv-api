package biodiv.common;
// Generated 31 Jul, 2017 7:18:53 AM by Hibernate Tools 3.5.0.Final

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Language generated by hbm2java
 */
@Entity
@Table(name = "language", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "three_letter_code"))
public class Language {

	public static final String DEFAULT_LANGUAGE = "English";
	private static final Random NUMBER_GENERATOR = new Random();

	private long id;
	private String name;
	private String threeLetterCode;
	private String twoLetterCode;
	private Boolean isDirty = false;
	private String region;

	private static transient Language defaultLanguage;

	public Language() {
	}

	public Language(long id, String name, String threeLetterCode) {
		this.id = id;
		this.name = name;
		this.threeLetterCode = threeLetterCode;
	}

	public Language(long id, String name, String threeLetterCode, String twoLetterCode, Boolean isDirty,
			String region) {
		this.id = id;
		this.name = name;
		this.threeLetterCode = threeLetterCode;
		this.twoLetterCode = twoLetterCode;
		this.isDirty = isDirty;
		this.region = region;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@NotBlank(message = "name cannot be blank")
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "three_letter_code", unique = true, nullable = false)
	@NotBlank(message = "threeLetterCode cannot be blank")
	public String getThreeLetterCode() {
		return this.threeLetterCode;
	}

	public void setThreeLetterCode(String threeLetterCode) {
		this.threeLetterCode = threeLetterCode;
	}

	@Column(name = "two_letter_code")
	@NotBlank(message = "twoLetterCode cannot be blank")
	public String getTwoLetterCode() {
		return this.twoLetterCode;
	}

	public void setTwoLetterCode(String twoLetterCode) {
		this.twoLetterCode = twoLetterCode;
	}

	@Column(name = "is_dirty")
	@NotBlank(message = "isDirty cannot be blank")
	public Boolean getIsDirty() {
		return this.isDirty;
	}

	public void setIsDirty(Boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Column(name = "region")
	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Transient
	public Language getDefaultLanguage() {
		//TODO
		return null;
	}
	
	@Override
	public String toString() {
		return "Language [id=" + id + ", name=" + name + "]";
	}

}