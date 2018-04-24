package biodiv.maps;

import java.util.Set;

/**
 * Query of the form that a key can have multiple values
 * 
 * @author mukund
 */
public class MapBoolQuery extends MapQuery {

	private Set<Object> values;

	public MapBoolQuery() {}
	
	public MapBoolQuery(String key, Set<Object> values, String path) {
		super(key, path);
		this.values = values;
	}

	public Set<Object> getValues() {
		return values;
	}

	public void setValues(Set<Object> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "MapBoolQuery [key=" + getKey() + ", values=" + values + ", path=" + getPath() + "]";
	}

}
