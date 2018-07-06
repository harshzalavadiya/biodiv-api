package biodiv.observation;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.customField.CustomField;
import biodiv.user.User;

public class RecommendationVoteDao extends AbstractDao<RecommendationVote, Long> implements DaoInterface<RecommendationVote, Long>{
	
	@Inject
	public RecommendationVoteDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public RecommendationVote findById(Long id){
		RecommendationVote entity = (RecommendationVote) sessionFactory.getCurrentSession().get(RecommendationVote.class, id);
		return entity;
	}

	public RecommendationVote findByAuthorAndObservation(User author, Observation obv) {
		
		String hql = "from RecommendationVote rv where rv.user.id =:userId and rv.observation.id =:obvId";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("userId", author.getId());
		query.setParameter("obvId", obv.getId());
		List rvList =  query.getResultList();
		if(rvList.size()>0){
			return (RecommendationVote) rvList.get(0);
		}else{
			return null;
		}
		
	}
}

