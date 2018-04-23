package biodiv.traits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

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
	 * dummy
	 * @param isNotObservationTrait
	 * dummy
	 * @param showInObservation
	 * dummy
	 * @return
	 * dummy
	 */
	public List<TraitObject> list(Set<Long> traitIds, Boolean isNotObservationTrait, Boolean showInObservation) {
		// TODO Auto-generated method stub
		Query q;
		List<TraitObject> objs = new ArrayList<TraitObject>();
		for (Long id : traitIds) {
			TraitObject obj = new TraitObject();
			List<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
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
				HashMap<String, Object> res = new HashMap<String, Object>();
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
	 * dummy
	 * @return
	 * dummy
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
	 * dummy
	 * @param classificationId
	 * dummy
	 * @return
	 * dummy
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
	 * dummy
	 * @return
	 * dummy
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
	 * dummy
 * @param objectType
	 * dummy
 * @return
	 * dummy
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
	 * @param objectId
	 * dummy
	 * @param objectType
	 * dummy
	 * @param traitId
	 * dummy
	 * @return
	 * dummy
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
	 * dummy
	 * @param objectType
	 * dummy
	 * @param trait
	 * dummy
	 * @return
	 * dummy
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

	public TraitValue getTraitValue(Long traitId, Long value) {
		// TODO Auto-generated method stub
		Query q;
		q=getCurrentSession().createQuery("from TraitValue where id=:value and traitId=:traitId");
		q.setParameter("value", value).setParameter("traitId", traitId);
		TraitValue results = null;
		try{
			 results=(TraitValue) q.getResultList().get(0);
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("array index out of bound or no element present");
		}
		
		return results;
	}

	public List<Trait> listObservationTrait() {
		// TODO Auto-generated method stub
		Query q;
		q=getCurrentSession().createQuery("From Trait where showInObservation=true and isNotObservationTrait=false and isDeleted=false");
		List<Trait> results=null;
		
			results=q.getResultList();
		

		return results;
	}


	

	public List<TraitValue> getTraitValueWithTraitId(Long id) {
		// TODO Auto-generated method stub
		Query q;
		q=getCurrentSession().createQuery("from TraitValue where traitId=:id");
		q.setParameter("id", id);
		List<TraitValue> results=q.getResultList();
		return results;
	
	}



}
