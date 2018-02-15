package biodiv.common;

import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

public class LicenseDao extends AbstractDao<License, Long> implements DaoInterface<License, Long>{

	@Override
	public License findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public License findByName(String name) {
		// TODO Auto-generated method stub
		Query q;
		q=getCurrentSession().createQuery("from License where name=:name").setParameter("name", name);
		List <License> list=q.getResultList();
		if(list.size() == 1) return  list.get(0);
		else throw new NotFoundException("No license find with name : "+name);
		
	}

	
	
}
