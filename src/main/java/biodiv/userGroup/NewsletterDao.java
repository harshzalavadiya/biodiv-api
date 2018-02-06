package biodiv.userGroup;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class NewsletterDao extends AbstractDao<Newsletter,Long> implements DaoInterface<Newsletter,Long>{

	@Override
	public Newsletter findById(Long id) {
		Newsletter entity = (Newsletter) getCurrentSession().get(Newsletter.class, id);
		return entity;
	}

	public List<Object[]> getPages(Newsletter nl,Map<String, Object> params, String hql) {
		
		Query query = getCurrentSession().createQuery(hql);
		if((Integer)params.get("max") != null){
			query.setMaxResults((Integer)params.get("max"));		
		}
		if((Integer)params.get("offset") != null){
			query.setFirstResult((Integer)params.get("offset"));
		}
		query.setProperties(nl);
		List<Object[]> list = query.getResultList();
		return list;
	}
	
	

}
