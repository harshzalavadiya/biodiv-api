package biodiv.follow;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;


public class FollowDao extends AbstractDao<Follow, Long> implements DaoInterface<Follow, Long>{

	@Override
	public Follow findById(Long id) {
		Follow entity = (Follow) getCurrentSession().get(Follow.class, id);
		return entity;
	}

	public Boolean isFollowing(String objectToFollowType, Long objectToFollowId, long userId) {
		
		String hql = "select count(*) from Follow fo where fo.objectType =:objectType and fo.objectId =:id and fo.user.id =:userId";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("objectType", objectToFollowType );
		query.setParameter("id", objectToFollowId );
		query.setParameter("userId", userId);	
		Long count = (Long) query.getSingleResult();
		if(count > (long)0){
			return true;
		}
		else return false;
	
	}

}
