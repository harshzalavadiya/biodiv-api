package biodiv.observation;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ObservationResourceId implements java.io.Serializable {

	private long resourceId;
	private long observationId;
	
	public ObservationResourceId () {
		super();
	}
	public ObservationResourceId(long resourceId, long observationId) {
		super();
		this.resourceId = resourceId;
		this.observationId = observationId;
	}
	@Column(name = "resource_id", nullable = false)
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	@Column(name = "observation_id", nullable = false)
	public long getObservationId() {
		return observationId;
	}
	public void setObservationId(long observationId) {
		this.observationId = observationId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (observationId ^ (observationId >>> 32));
		result = prime * result + (int) (resourceId ^ (resourceId >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObservationResourceId other = (ObservationResourceId) obj;
		if (observationId != other.observationId)
			return false;
		if (resourceId != other.resourceId)
			return false;
		return true;
	}
	
}
