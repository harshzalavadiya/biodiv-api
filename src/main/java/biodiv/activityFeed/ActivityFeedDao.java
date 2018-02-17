package biodiv.activityFeed;

import java.util.List;
import java.util.Map;

//import javax.persistence.Query;

import org.hibernate.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class ActivityFeedDao extends AbstractDao<ActivityFeed, Long> implements DaoInterface<ActivityFeed, Long> {

	public ActivityFeedDao() {
		System.out.println("ActivityFeedDao constructor");
	}
	
	@Override
	public ActivityFeed findById(Long id) {

		ActivityFeed entity = (ActivityFeed) getCurrentSession().get(ActivityFeed.class, id);
		return entity;
	}

	public List<Object[]> getFeeds(ActivityFeed _af, String hql, long rhId, String rootHolderType, String feedType,
			String feedPermission, String feedOrder, long fhoId, String feedHomeObjectType, String refreshtype,
			String timeLine, long refTym, int max) {

		Query query = getCurrentSession().createQuery(hql);
		if (max != 0) {
			query.setMaxResults(max);
		}
		query.setProperties(_af);
		List<Object[]> listResult = query.getResultList();
		return listResult;
	}

	public long getFeedCount(ActivityFeed _af, String hql) {
		Query query = getCurrentSession().createQuery(hql);
		query.setProperties(_af);
		long listResult = (long) query.getSingleResult();
		return listResult;
	}

	public ActivityFeed findActivityFeed(String hql, Map<String, Object> params) {
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("activityType", (String) params.get("type"));
		query.setParameter("objectId", (Long) params.get("id"));
		ActivityFeed af = (ActivityFeed) query.getSingleResult();

		return af;
	}

}
