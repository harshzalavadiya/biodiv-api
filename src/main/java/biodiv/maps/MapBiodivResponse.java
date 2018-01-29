package biodiv.maps;

import java.util.List;

import biodiv.observation.ObservationListMapper;

public class MapBiodivResponse {

	public List<ObservationListMapper> documents;
	public long totalDocuments;
	public String geohashAggregation;

	public MapBiodivResponse(List<ObservationListMapper> documents, long totalDocuments, String geohashAggregation) {
		super();
		this.documents = documents;
		this.totalDocuments = totalDocuments;
		this.geohashAggregation = geohashAggregation;
	}

}
