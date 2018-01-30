package biodiv.userGroup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;

public class UserGroupDao  extends AbstractDao<UserGroup, Long> implements DaoInterface<UserGroup, Long>{

	@Override
	public UserGroup findById(Long id) {
		UserGroup entity = (UserGroup) getCurrentSession().get(UserGroup.class, id);
	
		return entity;
	}
	
	public List<UserGroup> userUserGroups(long userId){
	//	String hql = "select userGroup.id from UserGroupMemberRole where suser.id =:Id";
		String hql = " select ug from UserGroup ug inner join UserGroupMemberRole ugmr on ug = ugmr.userGroup where ugmr.user.id =:userId";
		
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("userId", userId );

		List<UserGroup> listResult = query.getResultList();
		return listResult;
	}
	
	public List<User> userList(long groupId, long roleId) {
		String hql = "select u from UserGroupMemberRole ugmr inner join User u on ugmr.user.id = u.id where ugmr.userGroup.id = :groupId and ugmr.role.id = :roleId";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("groupId", groupId);
		query.setParameter("roleId", roleId);
		List<User> listResult = query.getResultList();
		return listResult;
	}

	public List<UserGroup> findAllByFilterRuleIsNotNull() {
		String hql = "from UserGroup ug where ug.filterRule != null";
		Query query = getCurrentSession().createQuery(hql);
		System.out.println(query);
		List<UserGroup> listResult = query.getResultList();
		return listResult;
	}

	public Set<UserGroup> posttoGroups(String objectType,Object object,Set<UserGroup> allowed,Set<UserGroup> userGroupsContainingObv,Set<UserGroup> obvUsrGrps,String pullType,String submitType,String userGroups,String filterUrl) throws Exception {
	
		long[] userGroup = Arrays.asList(userGroups.split(",")).stream().map(String::trim).mapToLong(Long::parseLong).toArray();
		UserGroupService userGroupService = new UserGroupService();
		
        Set<UserGroup> newUsrGrps = new HashSet<UserGroup>();
        Set<UserGroup> updated = new HashSet<UserGroup>();
        
	    for(long ug : userGroup)
	    {
	    	UserGroup usrgrp = userGroupService.findById(ug);
	    	newUsrGrps.add(usrgrp);
	    }
	    
	    Set<UserGroup> intersect = new HashSet<>(allowed);
	    intersect.retainAll(newUsrGrps);
	   
	    if(submitType.equalsIgnoreCase("post"))
	    {
		    
		    Set<UserGroup> union = new HashSet<>(obvUsrGrps);
		    union.addAll(intersect);
		    
		    Set<UserGroup> _union = new HashSet<>(union);
		    _union.addAll(userGroupsContainingObv);
		    
		    updated = new HashSet<>(_union);
	    }
	    else if(submitType.equalsIgnoreCase("unpost"))
	    {
	    	Set<UserGroup> diff = new HashSet<>(obvUsrGrps);
	    	diff.removeAll(intersect);
	    	updated = new HashSet<>(diff); 	
	    }  
	    
		return updated;
	}
	
	public long[] findObjectIdsByFilterRule(Map<String, String> filterUrlMap) {
		long[] abc = {1,2,3};
		
		return abc;
	}

	public UserGroup findByName(String name) {
		// TODO Auto-generated method stub
		Query q;
		UserGroup results=null;
		q=getCurrentSession().createQuery("from UserGroup where webaddress=:name").setParameter("name",name);
		try{
			 results=(UserGroup) q.getResultList().get(0);
		}
		catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out.println("array index out of bound"+ " result not found");
		}
		
		
		return results;
	}

	
	
}