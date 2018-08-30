package biodiv.traits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import biodiv.common.Language;

@Entity
@Table(name = "trait_value_translation", schema = "public")
public class TraitValueTranslation implements java.io.Serializable{
	
	private long id;
	private long version;
	private String description;
	private Language language;
	private String source;
	private TraitValue traitValue;
	private String value;
	
	

	public TraitValueTranslation() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Version
	@Column(name = "version", nullable = false)
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	@Column(name = "description", nullable = true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id", nullable = false)
	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}
	
	

	
	@Column(name = "source", nullable = true)
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trait_value_id", nullable = false)
	public TraitValue getTraitValue() {
		return traitValue;
	}
	public void setTraitValue(TraitValue traitValue) {
		this.traitValue = traitValue;
	}

	@Column(name = "value", nullable = true)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
