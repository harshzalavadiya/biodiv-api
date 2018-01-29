package biodiv.observation;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import biodiv.resource.Resource;

@Entity
@Table(name = "observation_resource", schema = "public")
public class ObservationResource  implements java.io.Serializable  {
		private ObservationResourceId id;
		private Resource resourceId;
		private Observation observationId;
		
		public ObservationResource() {
			super();
		}
		public ObservationResource(ObservationResourceId id,Resource resourceId, Observation observationId) {
			super();
			this.id=id;
			this.resourceId = resourceId;
			this.observationId = observationId;
		}
		
		
		@EmbeddedId

		@AttributeOverrides({
				@AttributeOverride(name = "resourceId", column = @Column(name = "resource_id", nullable = false)),
				@AttributeOverride(name = "observationId", column = @Column(name = "observation_id", nullable = false)) })
		public ObservationResourceId getId() {
			return id;
		}
		public void setId(ObservationResourceId id) {
			this.id = id;
		}
		
		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name = "resource_id", nullable = false, insertable = false, updatable = false)
		public Resource getResourceId() {
			return resourceId;
		}
		
		public void setResourceId(Resource resourceId) {
			this.resourceId = resourceId;
		}
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "observation_id", nullable = false, insertable = false, updatable = false)
		public Observation getObservationId() {
			return observationId;
		}
		public void setObservationId(Observation observationId) {
			this.observationId = observationId;
		}
}
