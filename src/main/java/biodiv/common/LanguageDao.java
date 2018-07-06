package biodiv.common;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;


public class LanguageDao extends AbstractDao<Language, Long> {
	
	@Inject
	protected LanguageDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Language findById(Long id) {
		Language entity = (Language) sessionFactory.getCurrentSession().get(Language.class, id);
		return entity;
	}

	public Language findByTwoLetterCode(String code) {
		
		String hql = "select l from Language l where l.twoLetterCode =:twoLetterCode";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("twoLetterCode", code );
		Language result = (Language) query.getSingleResult();
		return result;
	}

}
