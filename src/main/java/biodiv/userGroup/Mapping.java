package biodiv.userGroup;

import biodiv.observation.ObservationController;

public class Mapping {

	private String type;

	public Mapping(String x) {
		this.type = x;
	}

	public Object getObject() {
		Object obj;
		String x = this.getType();
		switch(x){
			
		case "observation":
			obj = new ObservationController();
		default:
			obj = new ObservationController();
		}
		return obj;
	}

	public String getType() {
		
		return this.type;
	}

	

}
