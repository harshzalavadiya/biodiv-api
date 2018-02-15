package biodiv.common;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class SpeciesGroupDao extends AbstractDao<SpeciesGroup, Long> implements DaoInterface<SpeciesGroup, Long> {

	public SpeciesGroupDao() {
		System.out.println("SpeciesGroupDao constructor");
	}
	
	public List<SpeciesGroup> list() {
		List<SpeciesGroup> results = new ArrayList<SpeciesGroup>();
		Query q;
		q = getCurrentSession().createQuery("from SpeciesGroup where name <> 'All' order by name");
		results = q.getResultList();
		return results;
	}

	@Override
	public SpeciesGroup findById(Long id) {
		SpeciesGroup speciesGroup = getCurrentSession().get(SpeciesGroup.class, id);
		return speciesGroup;
	}
}