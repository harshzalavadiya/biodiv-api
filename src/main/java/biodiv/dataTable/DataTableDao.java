package biodiv.dataTable;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class DataTableDao extends AbstractDao<DataTable, Long> implements DaoInterface<DataTable, Long>{
	
	@Inject
	public DataTableDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	public DataTable findById(Long id){
		DataTable entity = (DataTable) sessionFactory.getCurrentSession().get(DataTable.class, id);
		return entity;
	}
	
}
