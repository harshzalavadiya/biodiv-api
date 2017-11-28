package biodiv.observation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

	public Observation posttoGroups(String submitType, String objectIds, String userGroups,long userId) throws Exception {
		long[] objectId = Arrays.asList(objectIds.split(",")).stream().map(String::trim).mapToLong(Long::parseLong).toArray();
		long[] userGroup = Arrays.asList(userGroups.split(",")).stream().map(String::trim).mapToLong(Long::parseLong).toArray();
		
		System.out.println("inside Dao");
	    Observation obv = findById(objectId[0]);
	    
	    UserGroupService userGroupService = new UserGroupService();
	    
	    Set<UserGroup> userGroupsWithFilterRule = userGroupService.findAllByFilterRuleIsNotNull();
	    Geometry topology = obv.getTopology();
	    Set<UserGroup> userGroupsContainingObv = UserGroup.findAllContainingObv(topology,userGroupsWithFilterRule);
	    Set<UserGroup> obvUsrGrps = obv.getUserGroups();
	    Set<UserGroup> newUsrGrps = new HashSet<UserGroup>();
	    
	    System.out.println("oooooooooooooooooooooooooooo");
	    System.out.println(userGroupService);
	    List<UserGroup> allowedUsrGrps =  userGroupService.userUserGroups(userId);
	    Set<UserGroup>  allowed = new HashSet<UserGroup>(allowedUsrGrps);
	    for(long x : userGroup)
	    {
	    	System.out.println(x);
	    	UserGroup usrgrp = userGroupService.findById(x);
	    	newUsrGrps.add(usrgrp);
	    }
	    Set<UserGroup> intersect = new HashSet<>(allowed);
	    intersect.retainAll(newUsrGrps);
	    
	    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
	    System.out.println(userGroupsWithFilterRule);
	    Set<UserGroup> union = new HashSet<>(obvUsrGrps);
	    union.addAll(intersect);
	    Set<UserGroup> _union = new HashSet<>(union);
	    _union.addAll(userGroupsWithFilterRule);
	    obv.setUserGroups(_union);
		return obv;
	}
	
/*	public List<Observation> findAll(int limit, int offset) {
=======
	/*
	public List<Observation> findAll(int limit, int offset) {
>>>>>>> b6040d04d637ec2831c0238a185df4c0d4f14f9f
		List<Observation> obvs = (List<Observation>) getCurrentSession().createQuery("from Observation")
				.setFirstResult(offset)
				.setMaxResults(limit)
				.list();
		
		return obvs;
	}
*/

}
