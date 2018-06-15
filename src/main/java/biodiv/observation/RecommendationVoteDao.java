package biodiv.observation;

import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.customField.CustomField;
import biodiv.user.User;

public class RecommendationVoteDao extends AbstractDao<RecommendationVote, Long> implements DaoInterface<RecommendationVote, Long>{
	
	@Override
	public RecommendationVote findById(Long id){
		RecommendationVote entity = (RecommendationVote) getCurrentSession().get(RecommendationVote.class, id);
		return entity;
	}

	public RecommendationVote findByAuthorAndObservation(User author, Observation obv) {
		
		String hql = "from RecommendationVote rv where rv.user.id =:userId and rv.observation.id =:obvId";
		Query query = getCurrentSession().createQuery(hql);
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

