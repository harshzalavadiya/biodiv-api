package biodiv.maps;

import java.util.List;

public class MapBoolQuery {

	private String key;
	
	private List<Object> values;

	public MapBoolQuery(String key, List<Object> values) {
		super();
		this.key = key;
		this.values = values;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}
}
