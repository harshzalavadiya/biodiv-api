package biodiv.follow;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;


public class FollowDao extends AbstractDao<Follow, Long> implements DaoInterface<Follow, Long>{

	@Inject
	public FollowDao(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Follow findById(Long id) {
		Follow entity = (Follow) sessionFactory.getCurrentSession().get(Follow.class, id);
		return entity;
	}

	public Boolean isFollowing(String objectToFollowType, Long objectToFollowId, long userId) {
		
		String hql = "select count(*) from Follow fo where fo.objectType =:objectType and fo.objectId =:id and fo.user.id =:userId";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("objectType", objectToFollowType );
		query.setParameter("id", objectToFollowId );
		query.setParameter("userId", userId);	
		Long count = (Long) query.getSingleResult();
		if(count > (long)0){
			return true;
		}
		else return false;
	
	}

	public List<User> findAllFollowersOfObject(Long objectId, String objectToFollowType) {
		
		String hql = "select fo.user from Follow fo where fo.objectType =:objectType and fo.objectId =:id";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("objectType", objectToFollowType );
		query.setParameter("id", objectId );	
		List<User> followers = (List<User>) query.getResultList();
		return followers;
	}

}
