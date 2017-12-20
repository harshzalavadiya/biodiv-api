package biodiv.traits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.bouncycastle.crypto.tls.SecurityParameters;
import org.hibernate.Criteria;
import org.hibernate.Session;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class TraitDao extends AbstractDao<Trait, Long> implements DaoInterface<Trait, Long> {

	@Override
	public Trait findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param traitIds
	 * @param isNotObservationTrait
	 * @param showInObservation
	 * @return
	 */
	public List<TraitObject> list(Set<Long> traitIds, Boolean isNotObservationTrait, Boolean showInObservation) {
		// TODO Auto-generated method stub
		Query q;
		List<TraitObject> objs = new ArrayList<TraitObject>();
		for (Long id : traitIds) {
			TraitObject obj = new TraitObject();
			List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
			List<Object[]> data1 = new ArrayList<Object[]>();
			q = getCurrentSession().createQuery(
					"select name,traitTypes,description,icon,ontologyUrl,isParticipatory,id,showInObservation,isNotObservationTrait from Trait where isNotObservationTrait=:isNotObservationTrait and showInObservation=:showInObservation and id=:id");
			data1 = q.setParameter("id", id).setParameter("isNotObservationTrait", isNotObservationTrait)
					.setParameter("showInObservation", showInObservation).getResultList();
			Long checkId = null;
			for (Object[] x : data1) {
				obj.setName(String.valueOf(x[0]));
				obj.setTraitTypes(String.valueOf(x[1]));
				obj.setDescription(String.valueOf(x[2]));
				obj.setIcon(String.valueOf(x[3]));
				obj.setOntologyUrl(String.valueOf(x[4]));
				obj.setIsParticipatory((Boolean) x[5]);
				obj.setId((long) x[6]);
				checkId = (long) x[6];
				obj.setIsNotObservationTrait((Boolean) x[8]);
				obj.setShowInObservation((Boolean) x[7]);
			}

			List<Object[]> data = new ArrayList<Object[]>();

			q = getCurrentSession().createQuery(
					"select tv.id, tv.value,tv.description,tv.icon,tv.source from TraitValue as tv where tv.traitId=:id ");
			data = q.setParameter("id", id).getResultList();
			for (Object[] x : data) {
				Map<String, Object> res = new HashMap<String, Object>();
				res.put("id", x[0]);
				res.put("value", x[1]);
				res.put("description", x[2]);
				res.put("icon", x[3]);
				res.put("source", x[4]);
				values.add(res);
			}

			obj.setValues(values);
			if (checkId != null) {
				objs.add(obj);
			}

		}

		return objs;
	}

	/**
	 * 
	 * @param sGroup
	 * @return
	 */
	public List<Long> getTaxonIds(Long sGroup) {
		// TODO Auto-generated method stub
		List<Long> taxonIds = new ArrayList<Long>();
		Query q;
		q = getCurrentSession().createQuery("select taxonId from SpeciesGroupMapping where speciesId=:sGroup");
		taxonIds = q.setParameter("sGroup", sGroup).getResultList();
		taxonIds.removeAll(Collections.singleton(null));
		return taxonIds;
	}

	/**
	 * 
	 * @param taxonIds
	 * @param classificationId
	 * @return
	 */
	public Set<Long> getPathToRoot(List<Long> taxonIds, Long classificationId) {
		Query q;
		List<String> path = new ArrayList<String>();
		Set<Long> taxonId = new HashSet<Long>();
		String paths;
		for (Long tId : taxonIds) {
			q = getCurrentSession().createQuery("select tR.path " + "from TaxonomyRegistry as tR where "
					+ "tR.taxonDefinitionId=:tId " + "and tR.classificationId=:classificationId");
			path = q.setParameter("tId", tId).setParameter("classificationId", classificationId).getResultList();
			paths = path.get(0);

			int i = 0;
			String newString = new String();
			for (i = 0; i < paths.length(); i++) {
				char c = paths.charAt(i);
				if (c != '_') {
					newString = newString + c;
				} else {
					taxonId.add(Long.parseLong(newString));
					newString = "";
				}
			}
			if (newString != null) {
				taxonId.add(Long.parseLong(newString));
			}
		}

		return taxonId;
	}

	/**
	 * 
	 * @param allTaxonIds
	 * @return
	 */
	public Set<Long> getTraitId(Set<Long> allTaxonIds) {
		// TODO Auto-generated method stub
		Query q;
		Set<Long> traitIds = new HashSet<Long>();
		List<Long> id = new ArrayList<Long>();
		List<Long> ids = new ArrayList<Long>();
		q = getCurrentSession().createQuery(
				"select t.trait.id from TraitTaxonomyDefinition as t,Trait as tt where tt.id=t.trait.id and t.taxonomyDefinition.id in (:Ids)");
		id = q.setParameter("Ids", allTaxonIds).getResultList();
		traitIds.addAll(id);
		traitIds.addAll(id);
		q = getCurrentSession().createQuery(
				"select t.id from Trait  t left join TraitTaxonomyDefinition tt  on tt.trait.id=t.id where tt.taxonomyDefinition.id IS NULL");
		ids = q.getResultList();
		traitIds.addAll(ids);
		return traitIds;
	}

	/**
	 * 
	 * @param id
	 * @param objectType
	 * @return
	 */

	public List<Fact> getFact(Long id, String objectType) {
		List<Fact> results = new ArrayList<Fact>();
		Query q;
		q = getCurrentSession()
				.createQuery("from Fact where isDeleted=false and objectType=:objectType and objectId=:id");
		results = q.setParameter("id", id).setParameter("objectType", objectType).getResultList();
		return results;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Trait list(Long id) {
		// TODO Auto-generated method stub
		Trait trait = getCurrentSession().get(Trait.class, id);
		return trait;
	}

	/**
	 * 
	 * @param objectId
	 * @param objectType
	 * @param traitId
	 * @return
	 */
	public List<Fact> getFact(Long objectId, String objectType, Long traitId) {
		Query q;
		q = getCurrentSession()
				.createQuery("from Fact where objectId=:objectId and objectType=:objectType and trait.id=:traitId")
				.setParameter("objectId", objectId).setParameter("objectType", objectType)
				.setParameter("traitId", traitId);
		List<Fact> Listfact = q.getResultList();

		return Listfact;
	}

	/**
	 * 
	 * @param objectId
	 * @param objectType
	 * @param trait
	 * @return
	 */

	public int deleteFact(Long objectId, String objectType, Long trait) {
		// TODO Auto-generated method stub
		Query q;
		q = getCurrentSession()
				.createQuery("delete Fact where objectId=:objectId and trait.id=:trait and objectType=:objectType");
		q.setParameter("objectId", objectId).setParameter("objectType", objectType).setParameter("trait", trait);
		int result = q.executeUpdate();
		return result;

	}

	/**
	 * 
	 * @param traitValues
	 * @param traitId
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	
	

	public TraitValue getTraitValue(Long id) {
		// TODO Auto-generated method stub
		TraitValue traitValue = getCurrentSession().get(TraitValue.class, id);
		return traitValue;
	}

	public Serializable updateFact(Fact newupdated) {
		
		Serializable result = getCurrentSession().save(newupdated);

		return result;
	}

}
