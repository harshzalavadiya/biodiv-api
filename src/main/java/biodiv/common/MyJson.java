package biodiv.common;

import java.io.Serializable;


public class MyJson implements java.io.Serializable{


	private Long aid;
	private String name;
	private Long ro_id;
	private String ro_type;
	private String description;
	private String is_migrated;
	private String activity_performed;
	private Boolean is_scientific_name;

	public MyJson(){
		
	}

	public Long getAid() {
		return aid;
	}

	public void setAid(Long aid) {
		this.aid = aid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getRo_id() {
		return ro_id;
	}

	public void setRo_id(Long ro_id) {
		this.ro_id = ro_id;
	}

	public String getRo_type() {
		return ro_type;
	}

	public void setRo_type(String ro_type) {
		this.ro_type = ro_type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIs_migrated() {
		return is_migrated;
	}

	public void setIs_migrated(String is_migrated) {
		this.is_migrated = is_migrated;
	}

	public String getActivity_performed() {
		return activity_performed;
	}

	public void setActivity_performed(String activity_performed) {
		this.activity_performed = activity_performed;
	}

	public Boolean getIs_scientific_name() {
		return is_scientific_name;
	}

	public void setIs_scientific_name(Boolean is_scientific_name) {
		this.is_scientific_name = is_scientific_name;
	}
}
