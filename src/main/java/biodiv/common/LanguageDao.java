package biodiv.common;

import java.util.List;

import javax.persistence.Query;


public class LanguageDao extends AbstractDao<Language, Long> {
	
	@Override
	public Language findById(Long id) {
		Language entity = (Language) getCurrentSession().get(Language.class, id);
		return entity;
	}

	public Language findByTwoLetterCode(String code) {
		
		String hql = "select l from Language l where l.twoLetterCode =:twoLetterCode";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("twoLetterCode", code );
		Language result = (Language) query.getSingleResult();
		return result;
	}

}
