package biodiv.observation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

}
