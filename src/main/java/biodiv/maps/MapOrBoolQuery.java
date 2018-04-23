package biodiv.maps;

import java.util.List;
import java.util.Set;

/**
 * There is "OR" between any two instances of this query.
 * @author mukund
 *
 */
public class MapOrBoolQuery extends MapBoolQuery {

	public MapOrBoolQuery() {
		super();
	}

	public MapOrBoolQuery(String key, Set<Object> values) {
		this(key, values, null);
	}

	public MapOrBoolQuery(String key, Set<Object> values, String path) {
		super(key, values, path);
	}

}
