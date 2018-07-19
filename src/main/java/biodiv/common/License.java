package biodiv.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "license", schema = "public")
@Cache(region="species.License",usage = CacheConcurrencyStrategy.READ_ONLY,include="non-lazy")
public class License implements java.io.Serializable {

	private long id;
	private String name;
	private String url;

	public License() {
	}

	public License(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public License(long id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
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

	@Column(name = "url")
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "License [id=" + id + ", name=" + name + ", url=" + url + "]";
	}

}
