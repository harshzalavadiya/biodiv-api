package biodiv.user;

import biodiv.common.AbstractDao;
import biodiv.common.AbstractService;

public class RoleService extends AbstractService<Role>{

	private RoleDao roleDao;
	
	public RoleService(){
		this.roleDao = new RoleDao();
	}
	@Override
	public RoleDao getDao() {
		return roleDao;
	}
	
	public Role findRoleByAuthority(String authority) {
		
		try{
			Role role = getDao().findRoleByAuthority(authority);
			return role;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}

}
