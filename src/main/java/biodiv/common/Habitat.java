package biodiv.common;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "habitat", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Habitat implements java.io.Serializable {

	private long id;
	private int habitatOrder;
	private String name;

	public Habitat() {
	}

	public Habitat(long id, int habitatOrder, String name) {
		this.id = id;
		this.habitatOrder = habitatOrder;
		this.name = name;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "habitat_order", nullable = false)
	public int getHabitatOrder() {
		return this.habitatOrder;
	}

	public void setHabitatOrder(int habitatOrder) {
		this.habitatOrder = habitatOrder;
	}

	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Habitat [id=" + id + ", name=" + name + "]";
	}

}