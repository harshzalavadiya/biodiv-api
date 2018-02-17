package biodiv.allsearch;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;

public class AllSearchResponse {

	private Long count;
	private List<Map<String,Object>> eData;
	Map<String, Aggregation> aggregation;
	public AllSearchResponse(Long count, List<Map<String, Object>> eData, Map<String, Aggregation> aggregation) {
		super();
		this.count = count;
		this.eData = eData;
		this.aggregation = aggregation;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Map<String, Object>> geteData() {
		return eData;
	}
	public void seteData(List<Map<String, Object>> eData) {
		this.eData = eData;
	}
	public Map<String, Aggregation> getAggregation() {
		return aggregation;
	}
	public void setAggregation(Map<String, Aggregation> aggregation) {
		this.aggregation = aggregation;
	}
	
	
	
	
	
}
