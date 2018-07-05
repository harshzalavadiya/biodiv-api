package biodiv.rating;


import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;


public class RatingDao extends AbstractDao<Rating, Long> implements DaoInterface<Rating, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public RatingDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Rating findById(Long id) {
		Rating entity = (Rating) sessionFactory.getCurrentSession().get(Rating.class, id);
		System.out.println(entity);
		return entity;
	}
}
