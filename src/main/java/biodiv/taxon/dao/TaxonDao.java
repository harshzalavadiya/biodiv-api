package biodiv.taxon.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.taxon.datamodel.dao.Classification;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.taxon.datamodel.dao.TaxonomyRegistry;

public class TaxonDao extends AbstractDao<Taxon, Long> implements DaoInterface<Taxon, Long> {

	@Inject
	public TaxonDao(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Taxon findById(Long id) {
		Taxon entity = (Taxon) sessionFactory.getCurrentSession().get(Taxon.class, id);

		return entity;
	}

	public List<Object[]> list(Long parent, Long classificationId, Set<String> taxonIds, Boolean expand_taxon) {
		Query q;
		List<Object[]> taxon;
		List<Object[]> data = null;
		if (expand_taxon) {
			if (taxonIds != null) {
				taxonIds = getPathToRoot(classificationId, taxonIds);
				data = getAllTheNodes(classificationId, taxonIds);
				return data;
			}
		}
		if ((parent == null && taxonIds == null)) {
			q = sessionFactory.getCurrentSession().createQuery(
					"select t.id,t.name, t.rank, tR.path, tR.classificationId,tR.parent,t.position,t.speciesId"
							+ " from Taxon as t, TaxonomyRegistry as tR" + " where t.id=tR.taxonDefinitionId"
							+ " and t.isDeleted=false" + " and tR.classificationId=:classificationId"
							+ " and t.rank=0 and t.isDeleted=false order by t.name");
			taxon = q.setParameter("classificationId", classificationId).getResultList();
			return taxon;
		}

		if (parent != null) {
			q = sessionFactory.getCurrentSession().createQuery(
					"select t.id,t.name, t.rank, tR.path, tR.classificationId,tR.parent,t.position,t.speciesId"
							+ " from Taxon as t, TaxonomyRegistry as tR" + " where t.id=tR.taxonDefinitionId" + "  "
							+ " and tR.classificationId=:classificationId"
							+ " and tR.parent=:parent and t.isDeleted=false order by t.name");
			taxon = q.setParameter("parent", parent).setParameter("classificationId", classificationId).getResultList();
			return taxon;
		}
		return data;
	}

	private List<Object[]> getAllTheNodes(Long classificationId, Set<String> taxonIds) {
		// TODO Auto-generated method stub
		String queryForLike = new String();
		List<Object[]> result;
		Query q;
		int i = 0;
		for (String data : taxonIds) {
			int size = taxonIds.size();
			i++;
			String local;
			if (i == size) {
				local = " tR.parent= " + data;
			} else {
				local = " tR.parent=" + data + " or ";
			}
			queryForLike = queryForLike + local;
		}
		queryForLike = queryForLike + " or t.rank=0";
		q = sessionFactory.getCurrentSession()
				.createQuery("select t.id,t.name, t.rank, tR.path, tR.classificationId,tR.parent,t.position,t.speciesId"
						+ " from Taxon as t, TaxonomyRegistry as tR" + " where t.id=tR.taxonDefinitionId" + ""
						+ " and tR.classificationId=:classificationId and t.isDeleted=false and" + "(" + queryForLike
						+ ") order by t.rank,t.name");
		result = q.setParameter("classificationId", classificationId).getResultList();
		result = q.getResultList();

		return result;
	}

	/**
	 * 
	 * @param classificationId
	 *            dummy
	 * @param taxonIds
	 *            dummy
	 * @return dummy
	 */

	public Set<String> getPathToRoot(Long classificationId, Set<String> taxonIds) {
		Set<String> initialIds = new HashSet<String>();
		initialIds.addAll(taxonIds);
		List<String> path = new ArrayList<String>();
		taxonIds.clear();
		String paths;
		Query q;
		for (String s : initialIds) {
			Long tId = Long.parseLong(s);
			q = sessionFactory.getCurrentSession().createQuery("select tR.path " + "from TaxonomyRegistry as tR where "
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
					taxonIds.add(newString);
					newString = "";
				}
			}
			taxonIds.add(newString);
		}
		return taxonIds;

	}

	public List<Object[]> search(String term) {
		List<Object[]> results = new ArrayList<Object[]>();
		Query q;
		q = sessionFactory.getCurrentSession()
				.createQuery(
						"select t.name,t.status,t.position,t.id,t.rank from Taxon as t where lower(t.name) like :term order by t.name")
				.setMaxResults(10);
		results = q.setParameter("term", term.toLowerCase().trim() + '%').getResultList();

		return results;
	}

	public List<Object[]> specificSearch(Long classificationId, String term, Long taxonid) {

		List<Object[]> results = new ArrayList<Object[]>();
		Query q;
		if (taxonid != null) {
			q = sessionFactory.getCurrentSession().createQuery(
					"select t.id,t.status from Taxon as t where t.id=:taxonid and lower(t.name) like lower ('" + term
							+ "')");
			q.setParameter("taxonid", taxonid);
		} else {
			q = sessionFactory.getCurrentSession().createQuery(
					"select t.id,t.status from Taxon as t where lower(t.name) like lower ('" + term + "')");
		}
		results = q.getResultList();
		return results;
	}

	public Set<String> findAcceptedIds(Long classificationId, Set<String> taxonIds) {
		Query q;
		List<Long> newIds = new ArrayList<Long>();
		Set<String> initialIds = new HashSet<String>();
		initialIds.addAll(taxonIds);
		for (String s : initialIds) {
			Long tId = Long.parseLong(s);
			q = sessionFactory.getCurrentSession().createQuery("select acceptedId from AcceptedSynonyms where synonymId=:tId");
			newIds = q.setParameter("tId", tId).getResultList();

		}
		taxonIds.clear();
		for (Long ids : newIds) {
			taxonIds.add(String.valueOf(ids));
		}

		return taxonIds;
	}

	public List<Classification> classification() {
		// TODO Auto-generated method stub
		Query q;
		q = sessionFactory.getCurrentSession().createQuery("from Classification");
		List<Classification> results = q.getResultList();

		return results;
	}

	public List<Classification> classificationIdByName(String name) {

		Query q;

		q = sessionFactory.getCurrentSession().createQuery("from Classification where name=:name");
		List<Classification> results = q.setParameter("name", name).getResultList();

		return results;
	}

	public List<Object[]> getTaxonData(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		Query q;
		q = sessionFactory.getCurrentSession().createQuery("select name,id,status,position,rank from Taxon order by id")
				.setFirstResult(offset).setMaxResults(limit);
		List<Object[]> results = q.getResultList();
		return results;
	}

	public List<Taxon> getAllByTaxonDefinitionId(long id) {
		// TODO Auto-generated method stub
		Query q;
		q = sessionFactory.getCurrentSession().createQuery("select path From TaxonomyRegistry where taxonDefinitionId=:id");
		q.setParameter("id", id);
			List<String> paths=q.getResultList();
			Set<Long> idsa = new HashSet<Long>();
		for(String s : paths){
			List<String> ids = Arrays.asList(s.split("_"));
			for(String i :ids){
				idsa.add(Long.valueOf(i)) ;
			}
		}
		//when first query results in empty list
		if(idsa.size() <= 0){
			List<Taxon> result = new ArrayList<Taxon>();
			return result;
		}
		////when first query results in empty list
		for(Long idsad:idsa){
			System.out.println(idsad);
		}
		q=sessionFactory.getCurrentSession().createQuery("from Taxon where id in (:idsa)").setParameter("idsa",idsa);
				
		List<Taxon> result=q.getResultList();
		return result;
	}

	public TaxonomyRegistry getTaxonRegistryWithTaxonConceptId(Long id) {
		// TODO Auto-generated method stub
		Query q;
		q = sessionFactory.getCurrentSession().createQuery("From TaxonomyRegistry where taxonDefinitionId=:id");
		q.setParameter("id", id);
		List<TaxonomyRegistry> result=q.getResultList();
		if(result.size()>0){
			return result.get(0);
		}
		return null;
	}

}