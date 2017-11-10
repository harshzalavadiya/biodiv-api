package biodiv.common;

public class LanguageDao extends AbstractDao<Language, Long> {
	
	@Override
	public Language findById(Long id) {
		Language entity = (Language) getCurrentSession().get(Language.class, id);
		return entity;
	}

}
