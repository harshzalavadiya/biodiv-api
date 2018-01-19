package biodiv.maps;

import java.util.Set;

public class MapOrBoolQuery {
	private String key;

	private Set<String> values;
	
	

	public MapOrBoolQuery(String key, Set<String> values) {
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

	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

	
}
