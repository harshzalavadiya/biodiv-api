package biodiv.observation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.common.SpeciesGroup;
import biodiv.userGroup.UserGroup;
import net.minidev.json.JSONObject;

public class ObservationDao extends AbstractDao<Observation, Long> implements DaoInterface<Observation, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private ObservationListService observationListService;

	@Context
	private ResourceContext resourceContext;

	protected ObservationDao() {
		System.out.println("ObservationDao constructor");
	}

	@Override
	public Observation findById(Long id) {
		Observation entity = (Observation) getCurrentSession().get(Observation.class, id);
		System.out.println(entity);
		return entity;
	}

	public List<UserGroup> obvUserGroups(long id) {
		String hql = "select obv.userGroups from Observation obv where obv.id =:id";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("id", id);
		List<UserGroup> listResult = query.getResultList();
		System.out.println(listResult);
		return listResult;
	}

	public List<ObservationResource> getResource(long id) {
		// TODO Auto-generated method stub
		Query q;
		q = getCurrentSession()
				.createQuery("select obvr.resourceId from ObservationResource  as obvr where obvr.observationId.id=:id")
				.setParameter("id", id);
		List<ObservationResource> observationResources = q.getResultList();
		return observationResources;
	}

	public void updateGroup(Observation observation, SpeciesGroup speciesGroup) {
		if (speciesGroup != null && observation != null) {
			observation.setGroup(speciesGroup);
			Date date=new Date();
			observation.setLastRevised(date);
			
			JSONObject obj = new JSONObject();
			SimpleDateFormat out = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
			SimpleDateFormat in = new SimpleDateFormat("EEE MMM dd YYYY HH:mm:ss");
			
			String newDate=out.format(observation.getLastRevised());
			System.out.println("************************************");
			System.out.println(observation.getLastRevised());
			System.out.println("************************************");
			obj.put("lastrevised",newDate);
			obj.put("speciesgroupid", observation.getGroup().getId());
			obj.put("speciesgroupname", observation.getGroup().getName());
			observationListService.update("observation", "observation", observation.getId().toString(), obj.toString());
		}
		//Object data = observationListService.fetch("observation", "observation", observation.getId().toString());

		return ;

	}

	public List<Object[]> getRecommendationVotes(String q, Boolean singleObv, Long obvId, String allObvs) {

		String hql = q;
		// LongStream obvs =
		// Arrays.asList(allObvs.split(",")).stream().map(String::trim).mapToLong(Long::parseLong);
		// List<Long> obvs = Arrays.asList(allObvs.split(","));
		Query query = getCurrentSession().createSQLQuery(hql);
		if (singleObv == true) {
			query.setParameter("obvId", obvId);
		} else {
			// query.setParameter("obvs",allObvs);
		}
		// query.setMaxResults(5);
		List<Object[]> listResult = query.getResultList();
		// System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		// System.out.println(listResult.get(0));
		return listResult;
	}

	public Map<String,Object> calculateMaxVotedSpeciesName(Observation obv) {
		
		String hql = "select rv.recommendationByRecommendationId.id AS reco , count(rv.recommendationByRecommendationId) AS voteCount from RecommendationVote rv where "
				+"rv.observation.id =:obvId GROUP BY rv.recommendationByRecommendationId.id ORDER BY voteCount DESC";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("obvId",obv.getId());
		//System.out.println("query "+query);
		List<Object[]> recos = query.getResultList();
		
		
		if(recos == null || recos.isEmpty()){
			 return null;
		 }
		Map<String,Object> toReturn = new HashMap<String,Object>();
		List<Long> recoIds = new ArrayList<Long>();
		int noOfIdentifications = obv.getNoOfIdentifications();
		
		int maxCount =  ((Long) ((Object[]) recos.get(0))[1]).intValue();
		
		for(Object[] reco :recos){
			
			if(((Long) reco[1]).intValue() == maxCount){
				
				recoIds.add( (Long) reco[0]);	
				
			}
			noOfIdentifications += ((Long) reco[1]).intValue();
			
		}
		
		
		if(recos.size() ==1){
			toReturn.put("reco", recoIds.get(0));
			toReturn.put("noOfIdentifications", noOfIdentifications);
			return toReturn;
		}
		//List<Long> ids = new ArrayList<Long>();
		
		
		//if more than one max_voted,getting the latest one
		
		hql = "from RecommendationVote rv where rv.observation =:obv and rv.recommendationByRecommendationId.id in (:ids) order by rv.votedOn desc";
		Query query1 =  getCurrentSession().createQuery(hql);
		query1.setParameter("obv",obv);
		query1.setParameter("ids",recoIds);
		List<RecommendationVote> lrr =  query1.getResultList();
		RecommendationVote rr = lrr.get(0);
		toReturn.put("reco", rr.getRecommendationByRecommendationId().getId());
		toReturn.put("noOfIdentifications", noOfIdentifications);
		toReturn.put("haveToUpdateChecklistAnnotation", true);
		toReturn.put("recoVoteForChecklist", rr);
		return toReturn;
		
	}

}
