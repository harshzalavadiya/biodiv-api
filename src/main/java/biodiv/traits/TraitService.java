package biodiv.traits;

import java.util.List;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;

public class TraitService extends AbstractService<Trait>{

	private TraitDao traitDao;
	
	 public TraitService() {
		// TODO Auto-generated constructor stub
		this.traitDao=new TraitDao();
	}

	@Override
	public AbstractDao<Trait, Long> getDao() {
		// TODO Auto-generated method stub
		return traitDao;
	}
	public List<Object[]> list() {
		List<Object[]> result=traitDao.list();
		
		return result;
	}

}
