package biodiv.speciesPermission;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.user.User;

public class SpeciesPermissionDao extends AbstractDao<SpeciesPermission, Long> implements DaoInterface<SpeciesPermission, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public SpeciesPermissionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesPermission findById(Long id) {
		SpeciesPermission entity = (SpeciesPermission) sessionFactory.getCurrentSession().get(SpeciesPermission.class, id);

		return entity;
	}

	public List<SpeciesPermission> getAllSpeciesPermission(User currentUser, List<String> permissions,List<Long> parentTaxonIds) {
		
		String hql = "from SpeciesPermission sp where sp.user =:user and sp.permissionType in (:permissions) "
				+ "and sp.taxon.id in (:parentTaxonIds)";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("user", currentUser);
		query.setParameter("permissions", permissions);
		query.setParameter("parentTaxonIds", parentTaxonIds);
		List<SpeciesPermission> listResult = query.getResultList();
		return listResult;
	}
}
