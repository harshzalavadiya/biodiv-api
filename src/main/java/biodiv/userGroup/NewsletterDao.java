package biodiv.userGroup;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;

public class NewsletterDao extends AbstractDao<Newsletter,Long> implements DaoInterface<Newsletter,Long>{

	@Inject
	public NewsletterDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Newsletter findById(Long id) {
		Newsletter entity = (Newsletter) sessionFactory.getCurrentSession().get(Newsletter.class, id);
		return entity;
	}

	public List<Object[]> getPages(Newsletter nl,Map<String, Object> params, String hql) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
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
