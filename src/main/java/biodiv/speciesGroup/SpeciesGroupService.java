package biodiv.speciesGroup;

import java.util.List;

import biodiv.common.AbstractService;
import biodiv.common.SpeciesGroup;


public class SpeciesGroupService extends AbstractService<SpeciesGroup>{
	
	private SpeciesGroupDao speciesGroupDao;

	public SpeciesGroupService() {
		this.speciesGroupDao = new SpeciesGroupDao();
	}
	@Override
	public SpeciesGroupDao getDao() {
		// TODO Auto-generated method stub
		return speciesGroupDao;
	}
		
	

public List<SpeciesGroup> list() {
	// TODO Auto-generated method stub
	try{
		speciesGroupDao.openCurrentSession();
		List<SpeciesGroup> results= speciesGroupDao.list();
		return results;
	}
	catch (Exception e) {
		// TODO: handle exception
		throw e;
	}
	finally {
		speciesGroupDao.closeCurrentSession();
	}
	
	
}

}
