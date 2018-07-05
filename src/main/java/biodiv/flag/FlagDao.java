package biodiv.flag;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.rating.Rating;
import biodiv.rating.RatingLink;

public class FlagDao extends AbstractDao<Flag, Long> implements DaoInterface<Flag, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public FlagDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public Flag findById(Long id) {
		Flag entity = (Flag) sessionFactory.getCurrentSession().get(Flag.class, id);
		System.out.println(entity);
		return entity;
	}

	public List<Flag> fetchOlderFlags(String objectType, long objectId) {
		
		String hql = "from Flag f where f.objectType =:objectType and f.objectId =:objectId";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("objectType", objectType);
		query.setParameter("objectId", objectId);
		List<Flag> listResult = query.getResultList();
		return listResult;
	}
}