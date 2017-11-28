package biodiv.traits;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class TraitDao extends  AbstractDao<Trait, Long> implements DaoInterface<Trait, Long> {

	@Override
	public Trait findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object[]> list() {
		// TODO Auto-generated method stub
		List<Object[]> results=new ArrayList<Object[]>();
		Query q;
		q=getCurrentSession().createQuery("select name,version from Trait").setMaxResults(10);
		results=q.getResultList();
		
		
		return results;
	}

}
