package biodiv.common;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "species_group", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class SpeciesGroup implements java.io.Serializable {

	private long id;
	private SpeciesGroup speciesGroup;
	private String name;
	private Integer groupOrder;

	public SpeciesGroup() {
	}

	public SpeciesGroup(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public SpeciesGroup(long id, SpeciesGroup speciesGroup, String name, Integer groupOrder) {
		this.id = id;
		this.speciesGroup = speciesGroup;
		this.name = name;
		this.groupOrder = groupOrder;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_group_id")
	public SpeciesGroup getSpeciesGroup() {
		return this.speciesGroup;
	}

	public void setSpeciesGroup(SpeciesGroup speciesGroup) {
		this.speciesGroup = speciesGroup;
	}

	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "group_order")
	public Integer getGroupOrder() {
		return this.groupOrder;
	}

	public void setGroupOrder(Integer groupOrder) {
		this.groupOrder = groupOrder;
	}

	@Override
	public String toString() {
		return "SpeciesGroup [id=" + id + ", name=" + name + "]";
	}

}