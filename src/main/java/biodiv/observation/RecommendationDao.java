package biodiv.observation;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class RecommendationDao extends AbstractDao<Recommendation, Long> implements DaoInterface<Recommendation, Long>{

	public RecommendationDao() {
		System.out.println("RecommendationDao constructor");
	}
	
	@Override
	public Recommendation findById(Long id) {
		Recommendation entity = (Recommendation) getCurrentSession().get(Recommendation.class, id);
		return entity;
	}

}
