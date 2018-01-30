package biodiv.customField;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import biodiv.user.User;
import biodiv.userGroup.UserGroup;
import biodiv.util.Utils;

/**
 * CustomField generated by hbm2java
 */
@Entity
@Table(name = "custom_field", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = { "user_group_id",
		"name" }))
public class CustomField implements java.io.Serializable {

	private long id;
	private long version;
	private UserGroup userGroup;
	private User user;
	private boolean allowedMultiple;
	private String dataType;
	private String defaultValue;
	private int displayOrder;
	private boolean isMandatory;
	private String name;
	private String notes;
	private String options;
	private Boolean allowedParticipation;
	
	public enum DataType {
		INTEGER ("Integer"),
		DECIMAL ("DECIMAL"),
		TEXT("Text"),
		PARAGRAPH_TEXT("Paragraph_text"),
		DATE("Date"),
		BOOLEAN("Boolean");
		
		private String value;

		DataType(String value) {
			this.value = value;
		}

		String value() {
			return this.value;
		}
//		static List toList() {
//			return [INTEGER, DECIMAL, TEXT, PARAGRAPH_TEXT, DATE]
//		}
		
		static DataType getDataType(String value){
			switch(value){
				case "INTEGER":
					return DataType.INTEGER;
				case "DECIMAL":
					return DataType.DECIMAL;
				case "BOOLEAN":
					return DataType.BOOLEAN;
				case "DATE":
					return DataType.DATE;
				case "TEXT":
					return DataType.TEXT;
				case "PARAGRAPH_TEXT":
					return DataType.PARAGRAPH_TEXT;
				default:
					return null;
			}	
		}
	}

	
	public CustomField() {
	}

	public CustomField(long id, UserGroup userGroup, User user, boolean allowedMultiple, String dataType,
			int displayOrder, boolean isMandatory, String name) {
		this.id = id;
		this.userGroup = userGroup;
		this.user = user;
		this.allowedMultiple = allowedMultiple;
		this.dataType = dataType;
		this.displayOrder = displayOrder;
		this.isMandatory = isMandatory;
		this.name = name;
	}

	public CustomField(long id, UserGroup userGroup, User user, boolean allowedMultiple, String dataType,
			String defaultValue, int displayOrder, boolean isMandatory, String name, String notes, String options,
			Boolean allowedParticipation) {
		this.id = id;
		this.userGroup = userGroup;
		this.user = user;
		this.allowedMultiple = allowedMultiple;
		this.dataType = dataType;
		this.defaultValue = defaultValue;
		this.displayOrder = displayOrder;
		this.isMandatory = isMandatory;
		this.name = name;
		this.notes = notes;
		this.options = options;
		this.allowedParticipation = allowedParticipation;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_group_id", nullable = false)
	public UserGroup getUserGroup() {
		return this.userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "allowed_multiple", nullable = false)
	public boolean isAllowedMultiple() {
		return this.allowedMultiple;
	}

	public void setAllowedMultiple(boolean allowedMultiple) {
		this.allowedMultiple = allowedMultiple;
	}

	@Column(name = "data_type", nullable = false)
	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Column(name = "default_value")
	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Column(name = "display_order", nullable = false)
	public int getDisplayOrder() {
		return this.displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Column(name = "is_mandatory", nullable = false)
	public boolean isIsMandatory() {
		return this.isMandatory;
	}

	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "notes")
	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "options")
	public String getOptions() {
		return this.options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	@Column(name = "allowed_participation")
	public Boolean getAllowedParticipation() {
		return this.allowedParticipation;
	}

	public void setAllowedParticipation(Boolean allowedParticipation) {
		this.allowedParticipation = allowedParticipation;
	}

	public static String getTableNameForGroup(Long ugId,Boolean isUpdateOrInsertQuery) {
		if(isUpdateOrInsertQuery){
			return "custom_fields_group_"+ugId;
			
		}else return "CustomFieldsGroup" + ugId;
	}

	public String fetchSqlColumnName(long cfId,Boolean isUpdateOrInsertQuery) {
		if(isUpdateOrInsertQuery){
			return "cf_"+cfId;
			
		}else return "id.cf"+cfId;
	}

	public Map<String, Object> fetchTypecastValue(String value,CustomField cf) {
		
		String psqlType;
		Object defVal = (value != null)?value.trim():cf.getDefaultValue();
		switch(cf.getDataType()){
		case "Integer":
			psqlType = "bigint";
			try{
				defVal = defVal !=null ? Integer.parseInt((String) defVal): null;
			}catch(Exception e){
				defVal = null;
			}
			break;
		case "DECIMAL":
			psqlType = "real";
			try{
				defVal = defVal !=null? Float.parseFloat((String) defVal):null;
			}catch(Exception e){
				defVal = null;
			}
			break;
		case "Boolean":
			psqlType = "boolean";
			defVal = defVal !=null? Boolean.parseBoolean((String) defVal): false;
			break;

		case "Date":
			psqlType = "timestamp without time zone";
			defVal =  parseDate(defVal);
			break;
		
		case "PARAGRAPH_TEXT":
			psqlType = "text";
			defVal = defVal !=null?defVal: "";
			break;
		
		case "Text":
			psqlType = "character varying(400)";
			defVal = defVal !=null?defVal: "";
			break;
			
		default:
			psqlType = "character varying(255)";
			defVal = defVal !=null?defVal: "";
			break;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("psqlType", psqlType);
		map.put("defaultValue", defVal);
		return map;
		
	}

	private Object parseDate(Object defVal) {
		Date date = Utils.parseDate(defVal,true);
		return null;
	}

}