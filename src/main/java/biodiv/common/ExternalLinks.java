package biodiv.common;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "external_links", schema = "public")
public class ExternalLinks implements java.io.Serializable {

	private long id;
	private String colId;
	private Date eolFetchDate;
	private String eolId;
	private String gbifId;
	private String itisId;
	private String iucnId;
	private String ncbiId;
	private Integer noOfDataObjects;
	private String frlhtUrl;

	public ExternalLinks() {
	}

	public ExternalLinks(long id) {
		this.id = id;
	}

	public ExternalLinks(long id, String colId, Date eolFetchDate, String eolId, String gbifId, String itisId,
			String iucnId, String ncbiId, Integer noOfDataObjects, String frlhtUrl, Set taxonomyDefinitions) {
		this.id = id;
		this.colId = colId;
		this.eolFetchDate = eolFetchDate;
		this.eolId = eolId;
		this.gbifId = gbifId;
		this.itisId = itisId;
		this.iucnId = iucnId;
		this.ncbiId = ncbiId;
		this.noOfDataObjects = noOfDataObjects;
		this.frlhtUrl = frlhtUrl;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "col_id")
	public String getColId() {
		return this.colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "eol_fetch_date", length = 29)
	public Date getEolFetchDate() {
		return this.eolFetchDate;
	}

	public void setEolFetchDate(Date eolFetchDate) {
		this.eolFetchDate = eolFetchDate;
	}

	@Column(name = "eol_id")
	public String getEolId() {
		return this.eolId;
	}

	public void setEolId(String eolId) {
		this.eolId = eolId;
	}

	@Column(name = "gbif_id")
	public String getGbifId() {
		return this.gbifId;
	}

	public void setGbifId(String gbifId) {
		this.gbifId = gbifId;
	}

	@Column(name = "itis_id")
	public String getItisId() {
		return this.itisId;
	}

	public void setItisId(String itisId) {
		this.itisId = itisId;
	}

	@Column(name = "iucn_id")
	public String getIucnId() {
		return this.iucnId;
	}

	public void setIucnId(String iucnId) {
		this.iucnId = iucnId;
	}

	@Column(name = "ncbi_id")
	public String getNcbiId() {
		return this.ncbiId;
	}

	public void setNcbiId(String ncbiId) {
		this.ncbiId = ncbiId;
	}

	@Column(name = "no_of_data_objects")
	public Integer getNoOfDataObjects() {
		return this.noOfDataObjects;
	}

	public void setNoOfDataObjects(Integer noOfDataObjects) {
		this.noOfDataObjects = noOfDataObjects;
	}

	@Column(name = "frlht_url")
	public String getFrlhtUrl() {
		return this.frlhtUrl;
	}

	public void setFrlhtUrl(String frlhtUrl) {
		this.frlhtUrl = frlhtUrl;
	}

}
