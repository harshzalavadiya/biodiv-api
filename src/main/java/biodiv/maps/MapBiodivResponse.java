package biodiv.maps;

import java.util.List;

import biodiv.observation.ObservationListMapper;

public class MapBiodivResponse {

	public List<ObservationListMapper> documents;
	public long totalDocuments;
	public String geohashAggregation;
	public String viewFilteredGeohashAggregation;
	
	public MapBiodivResponse(List<ObservationListMapper> documents, long totalDocuments, String geohashAggregation, String viewFilteredGeohashAggregation) {
		super();
		this.documents = documents;
		this.totalDocuments = totalDocuments;
		this.geohashAggregation = geohashAggregation;
		this.viewFilteredGeohashAggregation = viewFilteredGeohashAggregation;
	}

}
