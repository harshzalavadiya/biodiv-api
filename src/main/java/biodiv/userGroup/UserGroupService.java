package biodiv.userGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hk2.annotations.Service;

import biodiv.Intercept;
import biodiv.common.AbstractService;
import biodiv.user.User;

@Service
public class UserGroupService extends AbstractService<UserGroup> {

	
	private UserGroupDao userGroupDao;

	public UserGroupService() {
		this.userGroupDao = new UserGroupDao();
	}

	@Override
	public UserGroupDao getDao() {
		return userGroupDao;
	}

	@Intercept
	public List<UserGroup> userUserGroups(long userId) {
		
		
		//boolean localTransaction = false;
		try{
//			if((HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().getStatus()) != TransactionStatus.ACTIVE)
//			{
//				HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
//				localTransaction = true;
//			}
			List<UserGroup> usrGrp = userGroupDao.userUserGroups(userId);
//			if(localTransaction == true)
//			{
//				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit(); 
//			}
			return usrGrp;
		} catch (Exception e){
//			if(localTransaction == true)
//			{
//				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback(); 
//			}  
			throw e;
		} finally{
			
		}	
	}
	
	public List<User> userList(long groupId,long roleId){
		try{
			userGroupDao.openCurrentSession();
			List<User> usr = userGroupDao.userList(groupId,roleId);
			return usr;
		} catch (Exception e){
			throw e;
		} finally{
			userGroupDao.closeCurrentSession();
		}	
	}

	public Set<UserGroup> findAllByFilterRuleIsNotNull() throws Exception{
		
		try{
			List<UserGroup> userGroupsHavingFilterRule = userGroupDao.findAllByFilterRuleIsNotNull(); 
			Set<UserGroup>  userGroupsWithFilterRule = new HashSet<UserGroup>(userGroupsHavingFilterRule);
			return userGroupsWithFilterRule;
		} catch(Exception e){
			throw e;
		} finally{
			
		}
		
	}
}