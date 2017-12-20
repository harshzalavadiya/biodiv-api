package biodiv.traits;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * TraitTaxonomyDefinitionId generated by hbm2java
 */
@Embeddable
public class TraitTaxonomyDefinitionId implements java.io.Serializable {

	private Long traitTaxonId;
	private Long taxonomyDefinitionId;

	public TraitTaxonomyDefinitionId() {
	}

	public TraitTaxonomyDefinitionId(Long traitTaxonId, Long taxonomyDefinitionId) {
		this.traitTaxonId = traitTaxonId;
		this.taxonomyDefinitionId = taxonomyDefinitionId;
	}

	@Column(name = "trait_taxon_id")
	public Long getTraitTaxonId() {
		return this.traitTaxonId;
	}

	public void setTraitTaxonId(Long traitTaxonId) {
		this.traitTaxonId = traitTaxonId;
	}

	@Column(name = "taxonomy_definition_id")
	public Long getTaxonomyDefinitionId() {
		return this.taxonomyDefinitionId;
	}

	public void setTaxonomyDefinitionId(Long taxonomyDefinitionId) {
		this.taxonomyDefinitionId = taxonomyDefinitionId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TraitTaxonomyDefinitionId))
			return false;
		TraitTaxonomyDefinitionId castOther = (TraitTaxonomyDefinitionId) other;

		return ((this.getTraitTaxonId() == castOther.getTraitTaxonId()) || (this.getTraitTaxonId() != null
				&& castOther.getTraitTaxonId() != null && this.getTraitTaxonId().equals(castOther.getTraitTaxonId())))
				&& ((this.getTaxonomyDefinitionId() == castOther.getTaxonomyDefinitionId())
						|| (this.getTaxonomyDefinitionId() != null && castOther.getTaxonomyDefinitionId() != null
								&& this.getTaxonomyDefinitionId().equals(castOther.getTaxonomyDefinitionId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getTraitTaxonId() == null ? 0 : this.getTraitTaxonId().hashCode());
		result = 37 * result + (getTaxonomyDefinitionId() == null ? 0 : this.getTaxonomyDefinitionId().hashCode());
		return result;
	}

}