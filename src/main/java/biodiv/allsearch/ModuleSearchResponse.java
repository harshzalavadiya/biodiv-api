package biodiv.allsearch;

import java.util.List;
import java.util.Map;

public class ModuleSearchResponse {

	
	private Long count;
	private List<Map<String,Object>> eData;
	public ModuleSearchResponse(Long count, List<Map<String, Object>> eData) {
		super();
		this.count = count;
		this.eData = eData;
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
	
}
