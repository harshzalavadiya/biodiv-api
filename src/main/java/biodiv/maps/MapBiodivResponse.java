package biodiv.maps;

import java.util.List;

import biodiv.observation.ObservationListMapper;

public class MapBiodivResponse {

	public List<ObservationListMapper> documents;
	public long totalDocuments;
	public String geohashAggregation;
	public String viewFilteredGeohashAggregation;
	public String termsAggregation;

	public MapBiodivResponse() {}

	public MapBiodivResponse(List<ObservationListMapper> documents, long totalDocuments, String geohashAggregation, String viewFilteredGeohashAggregation, String termsAggregation) {
		super();
		this.documents = documents;
		this.totalDocuments = totalDocuments;
		this.geohashAggregation = geohashAggregation;
		this.viewFilteredGeohashAggregation = viewFilteredGeohashAggregation;
		this.termsAggregation = termsAggregation;
	}

}
