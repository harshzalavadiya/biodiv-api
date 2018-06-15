package biodiv.Checklists;

import java.util.List;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.customField.CustomField;
import biodiv.user.User;

public class ChecklistsDao extends AbstractDao<Checklists, Long> implements DaoInterface<Checklists, Long>{
	
	@Override
	public Checklists findById(Long id){
		Checklists entity = (Checklists) getCurrentSession().get(Checklists.class, id);
		return entity;
	}
	
}
