package biodiv.traits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;



/**
 * SpeciesGroupMapping generated by hbm2java
 */
@Entity
@Table(name = "species_group_mapping", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = { "rank",
		"taxon_name" }))
public class SpeciesGroupMapping implements java.io.Serializable {

	private long id;
	private long taxonId;
	private long speciesId;
	private int rank;
	private String taxonName;

	public SpeciesGroupMapping() {
	}

//	public SpeciesGroupMapping(long id, SpeciesGroup speciesGroup, int rank, String taxonName) {
//		this.id = id;
//		
//		this.rank = rank;
//		this.taxonName = taxonName;
//	}
//
//	public SpeciesGroupMapping(long id, Taxon taxonomyDefinition, SpeciesGroup speciesGroup, int rank,
//			String taxonName) {
//		this.id = id;
//		this.taxonomyDefinition = taxonomyDefinition;
//		this.speciesGroup = speciesGroup;
//		this.rank = rank;
//		this.taxonName = taxonName;
//	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "taxon_concept_id")
//	public Taxon getTaxonomyDefinition() {
//		return this.taxonomyDefinition;
//	}
//
//	public void setTaxonomyDefinition(Taxon taxonomyDefinition) {
//		this.taxonomyDefinition = taxonomyDefinition;
//	}
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "species_group_id", nullable = false)
//	public SpeciesGroup getSpeciesGroup() {
//		return this.speciesGroup;
//	}
//
//	public void setSpeciesGroup(SpeciesGroup speciesGroup) {
//		this.speciesGroup = speciesGroup;
//	}

	@Column(name = "rank", nullable = false)
	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Column(name = "taxon_name", nullable = false)
	public String getTaxonName() {
		return this.taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}
	@Column(name = "taxon_concept_id", nullable = false)
	public long getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(long taxonId) {
		this.taxonId = taxonId;
	}
	@Column(name = "species_group_id", nullable = false)
	public long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(long speciesId) {
		this.speciesId = speciesId;
	}

}

