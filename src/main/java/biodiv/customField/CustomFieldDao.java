package biodiv.customField;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import biodiv.common.AbstractDao;
import biodiv.common.DaoInterface;
import biodiv.userGroup.UserGroup;


public class CustomFieldDao extends AbstractDao<CustomField, Long> implements DaoInterface<CustomField, Long>{
	
	@Override
	public CustomField findById(Long id){
		CustomField entity = (CustomField) getCurrentSession().get(CustomField.class, id);
		return entity;
	}

	public Object fetchValue(String genericQuery, Long obvId) {
		String hql = genericQuery;
		Query query = getCurrentSession().createSQLQuery(hql);
		query.setParameter("obvId", obvId);
		List result = query.getResultList();
		if(result.size() == 0 || result.get(0) == null){
			return "";
		}
		else return result.get(0);
	}

	public Long isRowExist(String genericQuery, Long obvId) {
		String hql = genericQuery;
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("obvId", obvId);
		Long result = (Long) query.getSingleResult();
		return result;

	}

	public void updateOrInsertRow(String genericQuery, Map<String, Object> map, boolean haveToUpdate) {
		String hql = genericQuery;
		Query query = getCurrentSession().createSQLQuery(hql);
		query.setParameter("columnValue", map.get("columnValue"));
		query.setParameter("obvId", map.get("obvId"));
		query.executeUpdate();
	}

	public List<CustomField> fetchCustomFieldsByGroup(UserGroup ug) {
		
		String hql = "from CustomField cf where cf.userGroup.id =:ugId order by cf.id asc";
		Query query = getCurrentSession().createQuery(hql);
		query.setParameter("ugId", ug.getId());
		List<CustomField> cf = query.getResultList();
		return cf;
	}

	public List<CustomField> fetchAllCustomFields() {
		String hql = "from CustomField cf order by cf.id asc";
		Query query = getCurrentSession().createQuery(hql);
		
		List<CustomField> cf = query.getResultList();
		return cf;
	}
	
	

}
