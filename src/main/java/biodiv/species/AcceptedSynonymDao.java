package biodiv.species;

import java.util.List;

import javax.persistence.Query;


import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.taxon.datamodel.dao.Taxon;

public class AcceptedSynonymDao extends AbstractDao<AcceptedSynonym, Long> implements DaoInterface<AcceptedSynonym, Long>{
	
	@Override
	public AcceptedSynonym findById(Long id){
		AcceptedSynonym entity = (AcceptedSynonym) getCurrentSession().get(AcceptedSynonym.class, id);
		return entity;
	}

	public List<AcceptedSynonym> findAllBySynonym(Taxon synonym) {
		
		String hql = "from AcceptedSynonym asy where asy.taxonomyDefinitionBySynonymId =:synonym";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("synonym", synonym);
		List<AcceptedSynonym> las =  query.getResultList();	
		return las;
	}
	
}
