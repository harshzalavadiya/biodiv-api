package biodiv.maps;

public class MapOrRangeQuery {
	
	private String key;
	private Object start;
	private Object end;
	
	
	
	public MapOrRangeQuery(String key, Object start, Object end) {
		super();
		this.key = key;
		this.start = start;
		this.end = end;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getStart() {
		return start;
	}
	public void setStart(Object start) {
		this.start = start;
	}
	public Object getEnd() {
		return end;
	}
	public void setEnd(Object end) {
		this.end = end;
	}
	

}
