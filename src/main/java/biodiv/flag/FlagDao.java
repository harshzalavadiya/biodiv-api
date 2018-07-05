package biodiv.flag;

import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.rating.Rating;
import biodiv.rating.RatingLink;

public class FlagDao extends AbstractDao<Flag, Long> implements DaoInterface<Flag, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	protected FlagDao() {
		System.out.println("RatingDao constructor");
	}

	@Override
	public Flag findById(Long id) {
		Flag entity = (Flag) getCurrentSession().get(Flag.class, id);
		System.out.println(entity);
		return entity;
	}

	public List<Flag> fetchOlderFlags(String objectType, long objectId) {
		
		String hql = "from Flag f where f.objectType =:objectType and f.objectId =:objectId";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("objectType", objectType);
		query.setParameter("objectId", objectId);
		List<Flag> listResult = query.getResultList();
		return listResult;
	}
}