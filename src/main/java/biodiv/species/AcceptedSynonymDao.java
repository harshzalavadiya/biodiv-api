package biodiv.species;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.taxon.datamodel.dao.Taxon;

public class AcceptedSynonymDao extends AbstractDao<AcceptedSynonym, Long> implements DaoInterface<AcceptedSynonym, Long>{
	
	@Inject
	protected AcceptedSynonymDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public AcceptedSynonym findById(Long id){
		AcceptedSynonym entity = (AcceptedSynonym) sessionFactory.getCurrentSession().get(AcceptedSynonym.class, id);
		return entity;
	}

	public List<AcceptedSynonym> findAllBySynonym(Taxon synonym) {
		
		String hql = "from AcceptedSynonym asy where asy.taxonomyDefinitionBySynonymId =:synonym";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("synonym", synonym);
		List<AcceptedSynonym> las =  query.getResultList();	
		return las;
	}
	
}
