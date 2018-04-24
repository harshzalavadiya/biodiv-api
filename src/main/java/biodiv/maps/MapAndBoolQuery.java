package biodiv.maps;

import java.util.List;
import java.util.Set;

/**
 * There is "AND" between any two instances of this query.
 * @author mukund
 *
 */
public class MapAndBoolQuery extends MapBoolQuery {

	public MapAndBoolQuery() {
		super();
	}

	public MapAndBoolQuery(String key, Set<Object> values) {
		this(key, values, null);
	}

	public MapAndBoolQuery(String key, Set<Object> values, String path) {
		super(key, values, path);
	}

}
