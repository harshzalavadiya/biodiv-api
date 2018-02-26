package biodiv.observation;

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

	public Object updateGroup(Observation observation, SpeciesGroup speciesGroup) {
		observation.setGroup(speciesGroup);
		JSONObject obj = new JSONObject();
		obj.put("speciesgroupid", observation.getGroup().getId());
		obj.put("speciesgroupname", observation.getGroup().getName());
		observationListService.update("observation", "observation", observation.getId().toString(), obj.toString());

		Object data = observationListService.fetch("observation", "observation", observation.getId().toString());

		return data;

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
