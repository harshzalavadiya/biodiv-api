package biodiv.userGroup;

import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;

public class UserGroupDao  extends AbstractDao<UserGroup, Long> implements DaoInterface<UserGroup, Long>{

	@Override
	public UserGroup findById(Long id) {
		UserGroup entity = (UserGroup) getCurrentSession().get(UserGroup.class, id);
		System.out.println("7&&&&&&&&&&&&&&&&");
		return entity;
	}
	
	public List<UserGroup> userUserGroups(long userId){
	//	String hql = "select userGroup.id from UserGroupMemberRole where suser.id =:Id";
		System.out.println("inside Usergroup Dao");
		String hql = " select ug from UserGroup ug inner join UserGroupMemberRole ugmr on ug = ugmr.userGroup where ugmr.user.id =:userId";
		
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("userId", userId );
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(userId);
		System.out.println(query);
		List<UserGroup> listResult = query.getResultList();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&");
		return listResult;
	}
	
	public List<User> userList(long groupId, long roleId) {
		String hql = "select u from UserGroupMemberRole ugmr inner join User u on ugmr.user.id = u.id where ugmr.userGroup.id = :groupId and ugmr.role.id = :roleId";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("groupId", groupId);
		query.setParameter("roleId", roleId);
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(roleId);
		System.out.println(query);
		List<User> listResult = query.getResultList();
		return listResult;
	}

	public List<UserGroup> findAllByFilterRuleIsNotNull() {
		String hql = "from UserGroup ug where ug.filterRule != null";
		Query query = getCurrentSession().createQuery(hql);
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(query);
		List<UserGroup> listResult = query.getResultList();
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&");
		return listResult;
	}
	
}