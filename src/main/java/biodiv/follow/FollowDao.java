package biodiv.follow;

import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;
import biodiv.userGroup.UserGroup;


public class FollowDao extends AbstractDao<Follow, Long> implements DaoInterface<Follow, Long>{

	@Override
	public Follow findById(Long id) {
		Follow entity = (Follow) getCurrentSession().get(Follow.class, id);
		return entity;
	}

	public Boolean isFollowing(String objectToFollowType, Long objectToFollowId, long userId) {
		
		String hql = "select count(*) from Follow fo where fo.objectType =:objectType and fo.id =:id and fo.user.id =:userId";
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