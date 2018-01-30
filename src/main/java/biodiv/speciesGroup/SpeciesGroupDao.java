package biodiv.speciesGroup;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.common.SpeciesGroup;
import biodiv.userGroup.UserGroup;


public class SpeciesGroupDao extends  AbstractDao<SpeciesGroup, Long> implements DaoInterface<SpeciesGroup, Long>  {

	

	public List<SpeciesGroup> list() {
		// TODO Auto-generated method stub
		List<SpeciesGroup> results= new ArrayList<SpeciesGroup>();
		Query q;
		q=getCurrentSession().createQuery("from SpeciesGroup where name <> 'All' order by name");
		results=q.getResultList();
		return results;
	}
	@Override
	public SpeciesGroup findById(Long id) {
		SpeciesGroup speciesGroup=   getCurrentSession().get(SpeciesGroup.class, id);
		return speciesGroup;
	}
}