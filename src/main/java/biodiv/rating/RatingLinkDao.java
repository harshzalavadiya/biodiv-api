package biodiv.rating;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;

public class RatingLinkDao extends AbstractDao<RatingLink, Long> implements DaoInterface<RatingLink, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public RatingLinkDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public RatingLink findById(Long id) {
		RatingLink entity = (RatingLink) sessionFactory.getCurrentSession().get(RatingLink.class, id);
		System.out.println(entity);
		return entity;
	}

	public List<RatingLink> findWhoLiked(String type, long id) {
	
		String hql = "from RatingLink rl where rl.ratingRef =:id and rl.type =:type";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("id", id);
		query.setParameter("type", type);
		List<RatingLink> listResult = query.getResultList();
		return listResult;
	}
}