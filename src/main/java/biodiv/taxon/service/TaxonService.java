package biodiv.taxon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;
import biodiv.taxon.dao.TaxonDao;
import biodiv.taxon.datamodel.dao.Classification;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.taxon.datamodel.ui.TaxonRelation;
import biodiv.taxon.search.SearchTaxon;

public class TaxonService extends AbstractService<Taxon> {
	private final Logger log = LoggerFactory.getLogger(TaxonService.class);
	private static final String ID = "id";
	private static final String taxonid = "taxonid";
	private static final String text = "text";
	private static final String rank = "rank";
	private static final String path = "path";
	private static final String classification = "classification";
	private static final String totalPath = "totalPath";
	private static final String parent = "parent";
	private static final String position = "position";
	private static final String speciesId = "speciesId";
	private TaxonDao taxonDao;
	

	public TaxonService() {
		this.taxonDao = new TaxonDao();
	}
	
	
	@Override
	public TaxonDao getDao() {
		// TODO Auto-generated method stub
		return taxonDao;
	}
	

	/**
	 * get children
	 */
	public List<TaxonRelation> list(Long parent, Long classificationId) {
		return list(parent, classificationId, null, true);
	}

	/**
	 * get tree expanded for all taxonids
	 * 
	 * @param taxonIds
	 * @return
	 */
	public List<TaxonRelation> list(Set<String> taxonIds, Long classificationId) {
		return list(null, classificationId, taxonIds, true);
	}
	
	public List<TaxonRelation> list(Long classificationId) {
		return list(null, classificationId,null, false);
	}
	
	
	/**
	 * 
	 * @param parent
	 * @param classificationId
	 * @param taxonIds
	 * @param expand_taxon
	 * @return
	 */
	private List<TaxonRelation> list(Long parent, Long classificationId, Set<String> taxonIds, Boolean expand_taxon) {

		try {
			List<Object[]> taxonList = taxonDao.list(parent, classificationId, taxonIds, expand_taxon);
			Set<String> data = null;

			if (taxonIds != null) {
				data = taxonDao.getPathToRoot(classificationId, taxonIds);
			}

			List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

			for (Object[] t : taxonList) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put(ID, t[0]);
				m.put(taxonid, t[0]);
				m.put(text, t[1]);
				m.put(rank, t[2]);
				m.put(path, t[3]);
				m.put(classification, t[4]);
				m.put(position, t[6]);
				m.put(totalPath, data);

				Long parentId = 0L;
				Long speciesId=0L;
				if (t[5] != null) {
					m.put("parent", t[5]);
				} else {
					m.put("parent", parentId);
				}
				if (t[7] != null) {
					m.put("speciesId", t[7]);
				} else {
					m.put("speciesId", speciesId);
				}
				res.add(m);
			}

			List<TaxonRelation> inputItems = createInputItems(res);

			if (expand_taxon) {
				if (taxonIds != null) {
					List<TaxonRelation> outputItems = buildHierarchy(inputItems);
					return outputItems;
				}
			}
			return inputItems;

		} catch (Exception e) {
			throw e;
		} finally {
			taxonDao.closeCurrentSession();
		}

	}

	/**
	 * 
	 * @param items
	 * @return
	 */
	private List<TaxonRelation> buildHierarchy(List<TaxonRelation> items) {
		List<TaxonRelation> result = new ArrayList<TaxonRelation>();
		Map<Long, TaxonRelation> idItemMap = prepareIdItemMap(items);

		for (TaxonRelation item : items) {
			Long parentId = item.getParent();

			if (parentId == 0) {
				result.add(item);
			} else {
				idItemMap.get(parentId).addChild(item);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param items
	 * @return
	 */
	private Map<Long, TaxonRelation> prepareIdItemMap(List<TaxonRelation> items) {
		HashMap<Long, TaxonRelation> result = new HashMap<>();

		for (TaxonRelation eachItem : items) {
			result.put(Long.valueOf(eachItem.getId()), eachItem);
		}
		return result;

	}

	/**
	 * 
	 * @param res
	 * @return
	 */
	private List<TaxonRelation> createInputItems(List<Map<String, Object>> res) {
		List<TaxonRelation> result = new ArrayList<>();
		for (Map<String, Object> data : res) {
			result.add(new TaxonRelation((Long) data.get(taxonid), (String) data.get(path),
					(Long) data.get(parent), (String) data.get(text), (Long) data.get(classification),
					(Long) data.get(ID), (Integer) data.get(rank),(String)data.get(position),(Long)data.get(speciesId), (Set<String>) data.get(totalPath)));
		}
		return result;
	}

	/**
	 * 
	 * @param classification
	 * @param term
	 * @return
	 */
	public List<Map<String, Object>> search(String term) {
		
//		try {
//			taxonDao.openCurrentSession();
//			List<Object[]> result = new ArrayList<Object[]>();
//			
//			result = taxonDao.search(term);
//			
//			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
//
//			for (Object[] re : result) {
//				Map<String, Object> res = new HashMap<String, Object>();
//				res.put("name", (String) re[0]);
//				res.put("status",(String) re[1]);
//				res.put("position",(String) re[2]);
//				res.put("id",(Long)re[3]);
//				res.put("rank",(Integer)re[4]);
//				results.add(res);
//			}
//			
//			return results;
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			taxonDao.closeCurrentSession();
//		}
		SearchTaxon searchTaxon=new SearchTaxon();
		
			 List<Map<String, Object>> name= searchTaxon.search(term);
			return name;
		
		
		
	}

	/**
	 * 
	 * @param classification
	 * @param term
	 * @return
	 */
	public Set<String> specificSearch(Long classification, String term,Long taxonids) {
		//TODO:NULL check , FINALLY close connection, string constant
	
		List<Object[]> results = new ArrayList<Object[]>();
		if(taxonid==null){
			results = taxonDao.specificSearch(classification, term,null);
		}
		else{
			results = taxonDao.specificSearch(classification, term,taxonids);
		}
		
		Set<String> taxonIds = new HashSet<String>();
		boolean expand_taxon = true;
		for (Object[] result : results) {
			
			String status = (String) result[1];
			if (status.equalsIgnoreCase("ACCEPTED")) {
				taxonIds.add(String.valueOf(result[0]));
				
			} else {
				taxonIds.add(String.valueOf(result[0]));
				taxonIds = taxonDao.findAcceptedIds(classification, taxonIds);
				
			}
		}
		return taxonIds;
		
	}

	public List<Classification> classification() {
		// TODO Auto-ge
		taxonDao.openCurrentSession();
		List<Classification> results=taxonDao.classification();
		return results;
	}

	public Long classificationIdByName(String name) {
		List<Classification> results=taxonDao.classificationIdByName(name);
		Long id=results.get(0).getId();
		return id;
	}


	public List<Object[]> getTaxonData(Integer offset, Integer limit) {
		// TODO Auto-generated method stub
	
		List<Object[]> result=taxonDao.getTaxonData(offset,limit);
		
		return result;
	}

}
