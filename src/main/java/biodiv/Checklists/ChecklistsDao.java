package biodiv.Checklists;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.customField.CustomField;
import biodiv.user.User;

public class ChecklistsDao extends AbstractDao<Checklists, Long> implements DaoInterface<Checklists, Long>{
	
	@Inject
	protected ChecklistsDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public Checklists findById(Long id){
		Checklists entity = (Checklists) sessionFactory.getCurrentSession().get(Checklists.class, id);
		return entity;
	}
	
}
