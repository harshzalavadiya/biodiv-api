package biodiv.observation;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;

public class RecommendationService extends AbstractService<Recommendation>{

	private RecommendationDao recommendationDao;
	
	public RecommendationService(){
		this.recommendationDao = new RecommendationDao();
	}
	@Override
	public RecommendationDao getDao() {
		return recommendationDao;
	}

}
