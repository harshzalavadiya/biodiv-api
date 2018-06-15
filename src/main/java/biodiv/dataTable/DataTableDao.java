package biodiv.dataTable;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class DataTableDao extends AbstractDao<DataTable, Long> implements DaoInterface<DataTable, Long>{
	
	@Override
	public DataTable findById(Long id){
		DataTable entity = (DataTable) getCurrentSession().get(DataTable.class, id);
		return entity;
	}
	
}
