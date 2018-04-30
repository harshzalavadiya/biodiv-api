package biodiv.user;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.NoResultException;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class RoleDao extends AbstractDao<Role,Long> implements DaoInterface<Role,Long>{

	@Inject
	public RoleDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Role findById(Long id) {
		Role entity = (Role) sessionFactory.getCurrentSession().get(Role.class, id);
		return entity;
	}

	public Role findRoleByAuthority(String authority) {		
		String hql = "from Role r where r.authority =:authority";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("authority", authority);
		Role role = null;
        try {
            role = (Role) query.getSingleResult();
        } catch(NoResultException e) {
            e.printStackTrace();
        }
		return role;
	}

}
