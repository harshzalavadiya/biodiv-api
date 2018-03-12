package biodiv.speciesPermission;

import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.user.User;
import biodiv.userGroup.UserGroup;

public class SpeciesPermissionDao extends AbstractDao<SpeciesPermission, Long> implements DaoInterface<SpeciesPermission, Long> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	SpeciesPermissionDao() {
		log.trace("UserGroupDao constructor");
	}

	@Override
	public SpeciesPermission findById(Long id) {
		SpeciesPermission entity = (SpeciesPermission) getCurrentSession().get(SpeciesPermission.class, id);

		return entity;
	}

	public List<SpeciesPermission> getAllSpeciesPermission(User currentUser, List<String> permissions,List<String> parentTaxonIds) {
		
		String hql = "from species_permission sp where sp.author_id =:user and sp.permission_type in ("+ String.join(",",permissions)+") "
				+ "and sp.taxon_concept_id in ("+String.join(",",parentTaxonIds)+")";
		Query query = getCurrentSession().createSQLQuery(hql);
		query.setParameter("user", currentUser.getId());
		List<SpeciesPermission> listResult = query.getResultList();
		return listResult;
	}
}
