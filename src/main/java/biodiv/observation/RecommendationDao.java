package biodiv.observation;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class RecommendationDao extends AbstractDao<Recommendation, Long> implements DaoInterface<Recommendation, Long>{

	
	@Inject
	protected RecommendationDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Recommendation findById(Long id) {
		Recommendation entity = (Recommendation) sessionFactory.getCurrentSession().get(Recommendation.class, id);
		return entity;
	}

}
