package biodiv.common;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "ufile", schema = "public")
public class Ufile implements java.io.Serializable {

	private long id;
	private long version;
	private Integer downloads;
	private String mimetype;
	private String path;
	private String size;
	private int weight;

	public Ufile() {
	}

	public Ufile(long id, String path, String size, int weight) {
		this.id = id;
		this.path = path;
		this.size = size;
		this.weight = weight;
	}

	public Ufile(long id, Integer downloads, String mimetype, String path, String size, int weight) {
		this.id = id;
		this.downloads = downloads;
		this.mimetype = mimetype;
		this.path = path;
		this.size = size;
		this.weight = weight;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version", nullable = false)
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Column(name = "downloads")
	public Integer getDownloads() {
		return this.downloads;
	}

	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}

	@Column(name = "mimetype")
	public String getMimetype() {
		return this.mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Column(name = "path", nullable = false)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "size", nullable = false)
	public String getSize() {
		return this.size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Column(name = "weight", nullable = false)
	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Ufile [id=" + id + ", path=" + path + ", size=" + size + "]";
	}


}
