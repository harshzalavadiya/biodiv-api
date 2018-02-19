package biodiv.allsearch;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;

public class AllSearchResponse {

	private Long count;
	private List<Map<String,Object>> eData;
	List<Map<String,Object>> docCount;
	public AllSearchResponse(Long count, List<Map<String, Object>> eData, List<Map<String, Object>> docCount) {
		super();
		this.count = count;
		this.eData = eData;
		this.docCount = docCount;
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
	public List<Map<String, Object>> getDocCount() {
		return docCount;
	}
	public void setDocCount(List<Map<String, Object>> docCount) {
		this.docCount = docCount;
	}
	
	
}
