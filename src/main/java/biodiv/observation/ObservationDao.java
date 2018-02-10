package biodiv.observation;

import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import javax.persistence.Query;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.common.SpeciesGroup;
import biodiv.userGroup.UserGroup;
import net.minidev.json.JSONObject;

class ObservationDao extends AbstractDao<Observation, Long> implements DaoInterface<Observation, Long> {
	
	private static final Logger log = LoggerFactory.getLogger(ObservationDao.class);

	@Context
    private ResourceContext resourceContext;
	
	protected ObservationDao() {
		System.out.println("ObservationDao constructor");
	}
	
	@Override
	public Observation findById(Long id) {
		Observation entity = (Observation) getCurrentSession().get(Observation.class, id);
		return entity;
	}

	public List<UserGroup> obvUserGroups(long id) {
		String hql = "select obv.userGroups from Observation obv where obv.id =:id";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("id", id );
		List<UserGroup> listResult = query.getResultList();
		System.out.println(listResult);
		return listResult;
	}

	public List<Map<String, Object>> list() {
		// TODO Auto-generated method stub
		Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
		TransportClient client = new PreBuiltTransportClient(settings);
		try {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SearchResponse result = client.prepareSearch("ob").setTypes("data")
				.execute().actionGet();
		List<Map<String, Object>> esData = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : result.getHits()) {
			esData.add(hit.getSource());
		}
		client.close();
		
		return esData;
	}

	public List<ObservationResource> getResource(long id) {
		// TODO Auto-generated method stub
		Query q;
		q=getCurrentSession().createQuery("select obvr.resourceId from ObservationResource  as obvr where obvr.observationId.id=:id").setParameter("id",id);
		List<ObservationResource> observationResources=q.getResultList();
		return observationResources;
	}

	public Object updateGroup(Observation observation, SpeciesGroup speciesGroup) {
		ObservationList observationList=new ObservationList();
		observation.setGroup(speciesGroup);
		JSONObject obj = new JSONObject();
		obj.put("speciesgroupid", observation.getGroup().getId());
		obj.put("speciesgroupname", observation.getGroup().getName());
		observationList.update("observation", "observations", observation.getId().toString(),obj.toString());
		Object data=observationList.fetch("observation", "observations",observation.getId().toString());
		return data;
		
	}

	public List<Object[]> getRecommendationVotes(String q, Boolean singleObv, Long obvId,String allObvs) {
		
		String hql = q;
		//LongStream obvs = Arrays.asList(allObvs.split(",")).stream().map(String::trim).mapToLong(Long::parseLong);
		//List<Long> obvs =  Arrays.asList(allObvs.split(","));
		Query query =  getCurrentSession().createSQLQuery(hql);
		if(singleObv == true){
			query.setParameter("obvId", obvId);
		}else{
			//query.setParameter("obvs",allObvs);
		}
		//query.setMaxResults(5);
		List<Object[]> listResult = query.getResultList();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println(listResult.get(0));
		return listResult;
	}

	
	


}
