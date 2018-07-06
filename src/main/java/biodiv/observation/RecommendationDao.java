package biodiv.observation;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import javax.inject.Inject;

import org.hibernate.SessionFactory;


import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class RecommendationDao extends AbstractDao<Recommendation, Long> implements DaoInterface<Recommendation, Long>{

	
	@Inject
	protected RecommendationDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Recommendation findById(Long id) {
		Recommendation entity = (Recommendation) sessionFactory.getCurrentSession().get(Recommendation.class, id);
		return entity;
	}

	public List<Recommendation> fetchthisList(String hql, Map<String, Object> data) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if(data.containsKey("isScientificName")){
			query.setParameter("isScientificName", data.get("isScientificName"));
		}
		
		if(data.containsKey("lowercaseName")){
			query.setParameter("lowercaseName", data.get("lowercaseName"));
		}
		
		if(data.containsKey("languageId")){
			query.setParameter("languageId", data.get("languageId"));
		}
		
		if(data.containsKey("taxonConcept")){
			query.setParameter("taxonConcept", data.get("taxonConcept"));
		}
		
		if(data.containsKey("acceptedName")){
			query.setParameter("acceptedName", data.get("acceptedName"));
		}
		
		List<Recommendation> lr =  query.getResultList();	
		return lr;
	
	}

}
