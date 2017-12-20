package biodiv.traits;

import java.util.List;
import java.util.Map;

public class TraitObject {
	private long id;
	private String name;
	private String author;
	private String traitTypes;
	private String description;
	private String icon;
	private String ontologyUrl;
	private Boolean isParticipatory;
	private Boolean isNotObservationTrait;
	private Boolean showInObservation;
	
	private List<Map<String, Object>> values;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public List<Map<String, Object>> getValues() {
		return values;
	}
	public void setValues(List<Map<String, Object>> values) {
		this.values = values;
	}
	public String getTraitTypes() {
		return traitTypes;
	}
	public void setTraitTypes(String traitTypes) {
		this.traitTypes = traitTypes;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getOntologyUrl() {
		return ontologyUrl;
	}
	public void setOntologyUrl(String ontologyUrl) {
		this.ontologyUrl = ontologyUrl;
	}
	public Boolean getIsParticipatory() {
		return isParticipatory;
	}
	public void setIsParticipatory(Boolean isParticipatory) {
		this.isParticipatory = isParticipatory;
	}
	public Boolean getIsNotObservationTrait() {
		return isNotObservationTrait;
	}
	public void setIsNotObservationTrait(Boolean isNotObservationTrait) {
		this.isNotObservationTrait = isNotObservationTrait;
	}
	public Boolean getShowInObservation() {
		return showInObservation;
	}
	public void setShowInObservation(Boolean showInObservation) {
		this.showInObservation = showInObservation;
	}
	

}
