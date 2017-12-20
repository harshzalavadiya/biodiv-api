package biodiv.observation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.userGroup.UserGroup;
import biodiv.userGroup.UserGroupService;

class ObservationDao extends AbstractDao<Observation, Long> implements DaoInterface<Observation, Long> {
	
	private static final Logger log = LoggerFactory.getLogger(ObservationDao.class);

	@Context
    private ResourceContext resourceContext;
	
	
	
	@Override
	public Observation findById(Long id) {
		Observation entity = (Observation) getCurrentSession().get(Observation.class, id);
		return entity;
	}

	public List<UserGroup> obvUserGroups(long id) {
		String hql = "select obv.userGroups from Observation obv where obv.id =:id";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("id", id );
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(id);
		System.out.println(query);
		List<UserGroup> listResult = query.getResultList();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println(listResult);
		return listResult;
	}

	
	


}
