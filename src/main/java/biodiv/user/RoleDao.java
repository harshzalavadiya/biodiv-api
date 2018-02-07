package biodiv.user;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.userGroup.UserGroup;

public class RoleDao extends AbstractDao<Role,Long> implements DaoInterface<Role,Long>{

	@Override
	public Role findById(Long id) {
		Role entity = (Role) getCurrentSession().get(Role.class, id);
		return entity;
	}

	public Role findRoleByAuthority(String authority) {
		
		String hql = "from Role r where r.authority =:authority";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("authority", authority);
		Role role = (Role) query.getSingleResult();
		return role;
	}

}
