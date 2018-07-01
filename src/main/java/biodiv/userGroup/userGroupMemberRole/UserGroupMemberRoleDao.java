package biodiv.userGroup.userGroupMemberRole;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.Role;
import biodiv.user.User;
import biodiv.userGroup.UserGroup;

public class UserGroupMemberRoleDao extends AbstractDao<UserGroupMemberRole, Long>
		implements DaoInterface<UserGroupMemberRole, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public UserGroupMemberRoleDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public UserGroupMemberRole findById(Long id) {
		UserGroupMemberRole entity = (UserGroupMemberRole) sessionFactory.getCurrentSession()
				.get(UserGroupMemberRole.class, id);
		return entity;
	}

	public int updateRole(UserGroup userGroup, User user, Role role) {

		Query query = sessionFactory.getCurrentSession()
				.createQuery("UPDATE UserGroupMemberRole SET role=:role WHERE user=:user and userGroup=:userGroup");
		query.setParameter("role", role);
		query.setParameter("user", user);
		query.setParameter("userGroup", userGroup);
		return query.executeUpdate();
	}

	public UserGroupMemberRole getUserGroupMemberRole(User user, UserGroup ug) {
		Query q = sessionFactory.getCurrentSession().createQuery("from UserGroupMemberRole where user=:user and userGroup=:userGroup");
		q.setParameter("user", user);
		q.setParameter("userGroup", ug);
		
		UserGroupMemberRole ugmr = null;

        try {
            ugmr = (UserGroupMemberRole) q.getSingleResult();
        } catch(NoResultException e ) {
            //e.printStackTrace();
            //throw new NotFoundException(e);
        	ugmr = null;
        }
        return ugmr;
	}
}