package biodiv.userGroup.userGroupMemberRole;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractService;
import biodiv.user.Role;
import biodiv.user.User;
import biodiv.userGroup.UserGroup;

public class UserGroupMemberRoleService extends AbstractService<UserGroupMemberRole> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	Configuration config;

	@Inject
	private SessionFactory sessionFactory;
	
	private UserGroupMemberRoleDao userGroupMemberRoleDao;

	@Inject
	public UserGroupMemberRoleService(UserGroupMemberRoleDao dao) {
		super(dao);
		this.userGroupMemberRoleDao = dao;
	}

	public int updateRole(UserGroup ug, User user, Role role) {
		return userGroupMemberRoleDao.updateRole(ug, user, role);
	}

	public UserGroupMemberRole getUserGroupMemberRole(User user, UserGroup ug) {
		return this.userGroupMemberRoleDao.getUserGroupMemberRole(user, ug);
	}

	public UserGroupMemberRole addUserGroupMemberRole(UserGroup ug, User user, Role role) {
		System.out.println(ug);
		System.out.println(user);
		System.out.println(role);
		 UserGroupMemberRole ugmr = new UserGroupMemberRole(ug, user, role);
	        if(ugmr.save(sessionFactory) == null) {
	            log.error("Error while saving usergroupMemberRole");
	            return null;
	        } else {
	            return ugmr;
	        }
	}

}